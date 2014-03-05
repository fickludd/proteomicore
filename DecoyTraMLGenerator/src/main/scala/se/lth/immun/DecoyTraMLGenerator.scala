package se.lth.immun

import se.lth.immun.app.CLIApplication
import se.lth.immun.app.CommandlineArgumentException

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter

import java.io.File
import java.io.FileReader
import java.io.BufferedReader
import java.io.FileWriter
import java.io.BufferedWriter
import java.util.Properties

import se.lth.immun.traml.ghost._

import se.lth.immun.chem._

import collection.mutable.ArrayBuffer
import collection.mutable.Queue
import collection.JavaConversions._
import util.Random

object DecoyTraMLGenerator extends CLIApplication {

	var inTramlFile:File 		= null
	var inTraml:GhostTraML 		= null
	var outTramlFile:File 		= null
	var outTraml:GhostTraML 	= null
	var decoysPerGroup			= 3
	var q1Offset				= 0.0
	var q3Offset				= 0.0
	var mode					= "shuffle"
	var syncSwath				= false
	var mods:Seq[IModifier] 	= Nil
	
	def main(args:Array[String]):Unit = {
		
		var properties = new Properties
    	properties.load(this.getClass.getResourceAsStream("/pom.properties"))
    	
		arg("TRAML_FILE", s => {
			inTramlFile = new File(s)
			if (outTramlFile == null) {
				if (inTramlFile.toString.toLowerCase.endsWith(".traml")) 
					outTramlFile = new File(inTramlFile.toString.dropRight(6) + ".decoy.traml")
				else
					outTramlFile = new File(inTramlFile.toString + ".decoy.traml")
			}
			inTraml = GhostTraML.fromFile(new XmlReader(
					new BufferedReader(new FileReader(inTramlFile))
				))
		})
		
	    rest("MODS", rest => {
				mods = rest.flatMap(Modifier.fromString)
			}, false)
		
		opt("output", 
				"file where output traml should be saved (default: input traml .decoy.traml)", 
				s => {
					outTramlFile = new File(s)
				},
				"X")
		
		opt("decoy-factor", 
				"number of decoys to generate per input peptide", 
				s => {
					decoysPerGroup = s.toInt
				},
				"X")
		
		opt("mode", 
				"mode for decoy generation, reverse|shuffle|new|same (default: shuffle)", 
				s => {
					require(
						Array("new", "shuffle", "reverse", "same").contains(s), 
						"mode must be one of 'new', 'shuffle', 'same' or 'reverse'. Got '%s'".format(s))
					mode = s
				},
				"MODE")
		
		opt("offset-q1", 
				"offset decoy q1 values by +-X (default: 0.0)", 
				s => {
					q1Offset = s.toDouble
				},
				"X")
		
		opt("offset-q3", 
				"offset decoy q3 values by +-X (default: 0.0)", 
				s => {
					q3Offset = s.toDouble
				},
				"X")
		
		opt("offset-mz", 
				"offset decoy q1 and q3 values by +-X (default: 0.0)", 
				s => {
					q1Offset = s.toDouble
					q3Offset = q1Offset
				},
				"X")
		
		opt("swath-sync", 
				"place decoys in same 25 Da swath as original peptide (only used if mode = new or offset > 0.0)", 
				s => {
					syncSwath = true
				})
		
		val before 		= System.currentTimeMillis
    	val name 		= properties.getProperty("pom.name")
    	val version 	= properties.getProperty("pom.version")
    	
		try {
			parseArgs(name + " "+version, args)
		} catch {
			case cae:CommandlineArgumentException => {
				cae.printStackTrace(); return
			}
		}
		
		println(name + " "+version)
		println(" input traML file: "+inTramlFile)
    	println("output traML file: "+outTramlFile)
    	println()
		
    	
    	outTraml = new GhostTraML
    	val gProt = new GhostProtein
    	gProt.id 		= "decoy"
    	gProt.accession = "decoy"
		gProt.name 		= "decoy" 
		gProt.shortName = "decoy"
		gProt.sequence 	= ""
    	outTraml.proteins += gProt.id -> gProt
    	
    	val getDecoyPep = 
    		if 			(mode == "new") 	novel _
    		else if 	(mode == "shuffle") shuffle _
    		else if 	(mode == "reverse") reverse _
    		else if 	(mode == "same") 	same _
    		else throw new Exception("Unknown mode '%s'".format(mode))
    	
    	def sync(origQ1:Double, dq1:Double) = {
    		val origSwath 	= origQ1.toInt / 25
    		val dSwath 		= dq1.toInt / 25
    		if (origSwath != dSwath && syncSwath)
    			origSwath * 25.0 + dq1 % 25.0
    		else
    			dq1
    	}
    	
    	for (t <- inTraml.transitionGroups) {
    		var key 	= t._1
    		var ts 		= t._2
    		var inPep 	= inTraml.peptides(ts.head.peptideRef)
    		var p 		= toPeptide(inPep)
    		var ions 	= findIons(ts.map(_.q3), p)
    		try {
    			println("%8.3f %s | %s".format(key._1, p.toString, 
    					ions.map(i => i.molecule.fragmentType+""+i.molecule.ordinal).mkString(" ")))
    		} catch {
    			case e:Exception => {
    				println("ERROR IN peptide %8.3f %s | %s".format(key._1, p.toString, ts.map(x => "%.3f".format(x.q3)).mkString(" ")))
				e.printStackTrace
    			}
    		}
    		
    		for (i <- 0 until decoysPerGroup) {
    			var decoyP 	= Modifier.modify(getDecoyPep(p), mods.toArray)
    			val origQ1	= key._1
    			val dm 		= decoyP.monoisotopicMass
    			val q1z 	= math.round(dm / origQ1).toInt
    			val dq1mz 	= (dm + q1z * Constants.PROTON_WEIGHT) / q1z
    			val dq1 	= sync(origQ1, Ion.mz(decoyP, q1z) + (math.random - 1.0) * 2 * q1Offset)
    			
    			
    			var decoyQ3s = getQ3s(ions, decoyP)
    			var decoyPepRef = "DECOY_"+i+"_"+ts.head.peptideRef
    			outTraml.peptides += decoyPepRef -> toGhostPeptide(decoyPepRef, decoyP, inPep)
    			println("%10.3f %25s | %s".format(dq1, decoyP.toString, decoyQ3s.map(x => "%7.2f".format(x)).mkString(" ")))
    			for (j <- 0 until decoyQ3s.length) {
    				var gt = new GhostTransition
    				gt.id = "DECOY_"+i+"_"+ts(j).id
    				gt.q1 = dq1
    				gt.q3 = decoyQ3s(j)
    				gt.ions = ArrayBuffer("" + ions(j).molecule.fragmentType + ions(j).molecule.ordinal)
    				gt.ce = ts(j).ce
    				gt.peptideRef = decoyPepRef
    				gt.rtStart = ts(j).rtStart
    				gt.rtEnd = ts(j).rtEnd
    				gt.intensity = ts(j).intensity
    				outTraml.transitions += gt
    			}
    		}
    	}
		
		outTraml.write(new XmlWriter(new BufferedWriter(new FileWriter(outTramlFile))))
		
		val after = System.currentTimeMillis
		println("  time taken: "+(after-before)+"ms")
	}
	
	
	
	def findIons(q3s:Seq[Double], peptide:Peptide):Seq[Ion[PeptideFragment]] = {
		val fs = peptide.getFragments(Array(EPeptideFragment.y, EPeptideFragment.b))
		var fsz = (for {
				f <- fs
				z <- 1 until 4
			} yield (f, z)).toSet
		var retFs = new ArrayBuffer[Ion[PeptideFragment]]
		q3s.map(q3 => {
			var diff = Double.MaxValue
			var best:PeptideFragment = null
			var bestz = 0
			for ((f,z) <- fsz) {
				if (math.abs(f.mass - (z*q3 - z)) < diff) {
					diff = math.abs(f.mass - (z*q3 - z))
					best = f
					bestz = z
				}
			}
			if (best != null) {
				retFs += new Ion(best, bestz)
				fsz -= best -> bestz
			}
		})
		return retFs
	}
	
	
	
	def toPeptide(gp:GhostPeptide) = {
		new Peptide(gp.sequence.map(c => StandardAminoAcid.fromChar(c)).toArray)
	}
	
	
	
	def toGhostPeptide(id:String, p:Peptide, inPep:GhostPeptide):GhostPeptide = {
		var gp = new GhostPeptide
		gp.id = id
		gp.sequence = p.toString
		gp.proteins += "decoy"
		gp.tramlPeptide = inPep.tramlPeptide
		return gp
	}
	
	
	
	def shuffle(p:Peptide) = {
		var shuffled = Random.shuffle(p.aminoAcids.dropRight(1).toBuffer)
		shuffled += p.aminoAcids.last
		new Peptide(shuffled.toArray)
	}
	
	/**
	 * AA frequencies in entire UniProtKB according to 
	 * http://web.expasy.org/docs/relnotes/relstat.html
	 */
	val aaFreq = {
		import StandardAminoAcid._
		Array(
			Tuple2(A, 8.25),
			Tuple2(Q, 3.93),
			Tuple2(L, 9.66),
			Tuple2(S, 6.56),
			Tuple2(R, 5.53),
			Tuple2(E, 6.75),
			Tuple2(K, 5.84),
			Tuple2(T, 5.34),
			Tuple2(N, 4.06),
			Tuple2(G, 7.07),
			Tuple2(M, 2.42),
			Tuple2(W, 1.08),
			Tuple2(D, 5.45),
			Tuple2(H, 2.27),
			Tuple2(F, 3.86),
			Tuple2(Y, 2.92),
			Tuple2(C, 1.37),  
			Tuple2(I, 5.96),
			Tuple2(P, 4.70),
			Tuple2(V, 6.87))
	}
	
	def novel(p:Peptide) = {
		import StandardAminoAcid._
		
		def getAA:StandardAminoAcid = {
			val r = math.random * 100
			var cumsum = 0.0
			for ((aa, freq) <- aaFreq) {
				cumsum += freq
				if (r < cumsum)
					return aa
			}
			return aaFreq.last._1//throw new Exception("This definitely shouldn't happed!")
		}
		
		val m = p.monoisotopicMass
		val aas = new ArrayBuffer[StandardAminoAcid]
		aas += (if (math.random * (5.84+5.53) < 5.84) K else R)
		
		var m2 = 0.0
		while (m2 < m) {
			val aa = getAA
			m2 += aa.monoisotopicMass
			aas += aa
		}
		
		m2 += Constants.WATER_WEIGHT
		if (math.abs((m2 - aas.last.monoisotopicMass) - m) < math.abs(m2 - m))
			new Peptide(aas.init.reverse.toArray)
		else
			new Peptide(aas.reverse.toArray)
	}
	
	
	def reverse(p:Peptide) = {
		new Peptide(p.aminoAcids.dropRight(1).reverse :+ p.aminoAcids.last)
	}
	
	
	def same(p:Peptide) = p
	
	
	
	def getQ3s(ions:Seq[Ion[PeptideFragment]], p:Peptide):Seq[Double] = {
		var fs = p.getFragments(Array(EPeptideFragment.y, EPeptideFragment.b))
		var unused = new Queue[PeptideFragment]
		for (pf <- fs.filter(x => !ions.contains(x) && x.ordinal > 4)) unused.enqueue(pf)
		return ions.map(i => {
			Ion.mz(
				fs.find(_.same(i.molecule)).getOrElse(unused.dequeue), 
				i.numExtraProtons, 
				i.numExtraElectrons
			) + q3Offset * 2 * (math.random - 0.5)
		})
	}
}
