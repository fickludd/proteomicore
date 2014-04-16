package se.lth.immun

import se.lth.immun.files.Delimited
import se.lth.immun.app.CLIApplication
import se.lth.immun.app.CommandlineArgumentException

import se.lth.immun.xml.XmlWriter

import java.io.File
import java.io.FileReader
import java.io.Reader
import java.io.BufferedReader
import java.io.FileWriter
import java.io.BufferedWriter
import java.util.Properties
import collection.mutable.ArrayBuffer
import collection.mutable.Queue
import collection.mutable.HashSet

import se.lth.immun.traml.ghost._

object Csv2Traml extends CLIApplication {

	
	val Q1 = Array("q1", "precursormz")
	val Q3 = Array("q3", "productmz")
	val CE = Array("ce", "collisionenergy")
	val SEQ = Array("sequence", "peptidesequence", "seq", "peptide")
	val PROT = Array("proteinname", "protein", "accession", "proteinid")
	val FRAG = Array("fragment", "fragmention")
	val Q1Z = Array("q1z", "precursorcharge")
	val Q3Z = Array("q3z", "productcharge")
	val RT = Array("rt", "retentiontime", "ntime")
	val IC = Array("intensity", "area", "libraryintensity", "rel_area")
	val SCORE = Array("score", "ddbscore")
	val ACC = Array("acc", "accession", "proteinaccession")
	
	
	
	class Chars(
		val sep:Char,
		val quote:Char
	) {}
	
	class Cols(
		val q1:Int,
		val q3:Int,
		val ce:Int,
		val seq:Int,
		val prot:Int,
		val frag:Int,
		val q1z:Int 		= -1,
		val q3z:Int 		= -1,
		val rt:Int	 		= -1,
		val intensity:Int	= -1,
		val score:Int		= -1,
		val acc:Int			= -1
	) {
		require(q1 >= 0, "q1 column should be >= 0")
		require(q3 >= 0, "q3 column should be >= 0")
		require(ce >= 0, "ce column should be >= 0")
		require(seq >= 0, "seq column should be >= 0")
		require(prot >= 0, "prot column should be >= 0")
		require(frag >= 0, "frag column should be >= 0")
	}
	
	var inCsvFile:File 			= null
	var inCsv:BufferedReader 	= null
	var outTramlFile:File 		= null
	var outTraml:GhostTraML 	= null
	var rr:String => List[String] = null
	var hasHeader				= false
	
	def main(args:Array[String]):Unit = {
		
		var properties = new Properties
    	properties.load(this.getClass.getResourceAsStream("/pom.properties"))
    	
		arg("CSV_FILE", s => {
			inCsvFile = new File(s)
			if (outTramlFile == null) {
				if (inCsvFile.toString.toLowerCase.endsWith(".csv")) 
					outTramlFile = new File(inCsvFile.toString.dropRight(4) + ".traml")
				else
					outTramlFile = new File(inCsvFile.toString + ".traml")
			}
			
			inCsv = new BufferedReader(new FileReader(inCsvFile))
		})
		
		opt("output", 
				"file where output traml should be saved (default: input csv .traml)", 
				s => {
					outTramlFile = new File(s)
				},
				"X")
		
		
		val before 		= System.currentTimeMillis
    	val name 		= properties.getProperty("pom.name")
    	val version 	= properties.getProperty("pom.version")
    	
		try {
			parseArgs(name + " "+version, args)
		} catch {
			case cae:CommandlineArgumentException => 
				println
				println("understood columns (not case-sensistive):")
				println("    Q1    " + Q1.mkString(", "))
				println("    Q3    " + Q3.mkString(", "))
				println("    CE    " + CE.mkString(", "))
				println("    SEQ   " + SEQ.mkString(", "))
				println("    PROT  " + PROT.mkString(", "))
				println("    FRAG  " + FRAG.mkString(", "))
				println("    Q1Z   " + Q1Z.mkString(", "))
				println("    Q3Z   " + Q3Z.mkString(", "))
				println("    RT    " + RT.mkString(", "))
				println("    IC    " + IC.mkString(", "))
				println("    SCORE " + SCORE.mkString(", "))
				println("    ACC   " + ACC.mkString(", "))
				println
			
				return
		}
		
		println(name + " "+version)
		println("   input csv file: "+inCsvFile)
    	println("output traML file: "+outTramlFile)
    	println()
    	
    	
    	
    	readAhead(10)
    	rr 			= getDelimReader
    	val cols 	= guessCols
    	if (hasHeader) nextLine
    	outTraml 	= new GhostTraML
    	val ts 		= new HashSet[GhostTransition]
    	
    	var line = nextLine
    	while (line != null) {
    		val vals = rr(line)
    		val prot = vals(cols.prot)
    		if (!outTraml.proteins.contains(prot)) {
    			val gProt = new GhostProtein
		    	gProt.id 		= prot
		    	gProt.accession = 
		    		if (cols.acc >= 0) vals(cols.acc) else "unknown"
				gProt.name 		= prot 
				gProt.shortName = prot
				gProt.sequence 	= ""
		    	outTraml.proteins += gProt.id -> gProt
    		}
    		
    		val pep = vals(cols.seq)
    		if (!outTraml.peptides.contains(pep)) {
    			val gPep = new GhostPeptide
    			gPep.id = pep
    			gPep.sequence = pep
    			outTraml.peptides += gPep.id -> gPep
    		}
    		if (!outTraml.peptides(pep).proteins.contains(prot))
    			outTraml.peptides(pep).proteins += prot
    		
    		val gt = new GhostTransition
    		gt.q1 = vals(cols.q1).toDouble
    		gt.q3 = vals(cols.q3).toDouble
    		gt.ce = vals(cols.ce).toDouble
    		gt.id = "%s @ %.3f / %.3f".format(pep, gt.q1, gt.q3)
    		gt.peptideRef = pep
    		if (cols.rt >= 0) {
    			val rt = vals(cols.rt).toDouble
    			gt.rtStart = rt
    			gt.rtEnd = rt
    		}
    		if (cols.intensity >= 0)
    			gt.intensity = vals(cols.intensity).toDouble
    		
    		if (!ts.contains(gt))
    			ts += gt
    		
    		line = nextLine
    	}
		
		for (gt <- ts)
			outTraml.transitions += gt
		
		outTraml.write(new XmlWriter(new BufferedWriter(new FileWriter(outTramlFile))))
		
		val after = System.currentTimeMillis
		println("  time taken: "+(after-before)+"ms")
	}
	
	
	val nextLines = new Queue[String]
	def readAhead(n:Int):Unit = {
		for (i <- 0 until n) {
			val line = inCsv.readLine
			if (line == null)
				return 
			nextLines += line
		}
	}
	
	def nextLine:String = {
		if (nextLines.nonEmpty) nextLines.dequeue
		else 					inCsv.readLine
	}
	
	def getDelimReader:String => List[String] = {
		val commaCount 	= nextLines.map(_.count(_ == ',')).sum
		val tabCount 	= nextLines.map(_.count(_ == '\t')).sum
		(row:String) => Delimited.readRow(if (commaCount > tabCount) ',' else '\t', '"', row)
	}
	
	def guessCols:Cols = {
		def isNum(str:String) = {
			try {
				str.toDouble
				true
			} catch {
				case _:Throwable => false
			}
		}
		def isFrag(str:String) = {
			try {
				str.tail.toInt
				Array('a', 'b', 'c', 'x', 'y', 'z').contains(str.head)
			} catch {
				case _:Throwable => false
			}
		}
		
	
		val rows = nextLines.map(rr)
		hasHeader = !rows.head.exists(isNum)
		val protRE		= "(.*[a-z].*[a-z].*)|(.*[A-Z].*[0-9].*)".r
		
		if (hasHeader) {
			val header = rows.head
			def indexOf(l:Seq[String]) = 
				header.indexWhere(x => l.contains(x.toLowerCase))
			
			return new Cols(
					indexOf(Q1),
					indexOf(Q3),
					indexOf(CE),
					indexOf(SEQ),
					indexOf(PROT),
					indexOf(FRAG),
					indexOf(Q1Z),
					indexOf(Q3Z),
					indexOf(RT),
					indexOf(IC),
					indexOf(SCORE),
					indexOf(ACC)
				)
		} else {
			val first 		= rows.head
			val cols 		= rows.transpose
			val numCols 	= cols.filter(_.forall(isNum))
			cols.indexWhere(_.forall(x => x.contains('a' to 'z') ))
			return new Cols(
					first.indexOf(numCols(0)(0)),
					first.indexOf(numCols(1)(0)),
					first.indexOf(numCols(2)(0)),
					first.indexWhere(_.forall("GASPVTILNDKQEMHFRYWC".toCharArray.contains)),
					cols.indexWhere(_.forall(x => protRE.findFirstIn(x).nonEmpty)),
					cols.indexWhere(_.forall(isFrag))
				)
		}
	}
}