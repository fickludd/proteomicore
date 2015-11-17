package se.lth.immun.traml.clear

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashSet
import scala.collection.mutable.HashMap

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter

import se.lth.immun.traml._
import Clear._

object ClearTraML {
	
	case class PepCharge(cp:ClearPeptide, z:Option[Int])
	case class AssayInfo(q1:Double, q1z:Option[Int], ce:Option[Double], rt:Option[ClearRetentionTime.RT])
	
	def pepCharge(p:Peptide) = {
		val sequence = p.userParams.find(_.name == FULL_PEPTIDE_NAME).flatMap(_.value).getOrElse(p.id)
		val cp = new ClearPeptide(sequence, p.proteinRefs) 
		for {
			rt <- p.retentionTimes
			crt <- ClearRetentionTime.fromRetentionTime(rt)
		}
			cp.rt = Some(crt)
		PepCharge(
			cp,
			cvInt(CHARGE_STATE_ACC, p.cvParams)
		)
	}
	
	def fromFile(r:XmlReader, repFreq:Int = 0):ClearTraML = {
		val x = new ClearTraML
		
		x.traml = TraML.fromFile(r, repFreq)
		
		val refToID = new HashMap[String, PepCharge]
		def toPepCharge(pRef:Option[String], cRef:Option[String], id:String):PepCharge =
			refToID(
				pRef.orElse(cRef)
				.getOrElse(throw new Exception("%s is lacking both peptide and compound id".format(id)))
			)
		
		for (p <- x.traml.compoundList.peptides) yield {
			val pz = pepCharge(p)
			refToID(p.id) = pz
			x.compounds += pz.cp
			x.proteins ++= pz.cp.proteins
		}
		
		for (t <- x.traml.transitions) {
			val (aInfo, channel) = parseChannel(t)
			val pz = toPepCharge(t.peptideRef, t.compoundRef, t.id)
			val a = pz.cp.getAssay(
						aInfo.q1, 
						aInfo.q1z.orElse(pz.z).getOrElse(throw new Exception("No precursor charge found for transition '%s'!".format(t.id))),
						aInfo.ce
					)
				
			if (aInfo.rt.nonEmpty)
				pz.cp.rt = aInfo.rt
			a.ms2Channels += channel
		}
			
		if (x.traml.targetList.isDefined) {
			for (t <- x.traml.targetList.get.targetIncludes) {
				val (aInfo, channel) = parseChannel(t)
				val pz = toPepCharge(t.peptideRef, t.compoundRef, t.id)
				val a = pz.cp.getAssay(
						aInfo.q1, 
						aInfo.q1z.orElse(pz.z).getOrElse(throw new Exception("No precursor charge found for transition '%s'!".format(t.id))),
						aInfo.ce
					)
				
				if (aInfo.rt.nonEmpty)
					pz.cp.rt = aInfo.rt
				a.ms1Channels += channel
			}
		}
		
		x
	}
	
	def parseChannel(t:Transition) = {
		
		val q1 = cvDouble(ISOLATION_WINDOW_TARGET_ACC, t.precursor.cvParams)
		val q1z = cvInt(CHARGE_STATE_ACC, t.precursor.cvParams)
		val q3 = cvDouble(ISOLATION_WINDOW_TARGET_ACC, t.product.cvParams)
		val q3z = cvInt(CHARGE_STATE_ACC, t.product.cvParams)
		val expIntensity = 
			cvDouble(PRODUCT_ION_INTENSITY_ACC, t.cvParams)
				.orElse(cvDouble(PRODUCT_ION_INTENSITY_ACC, t.product.cvParams))
		val ce = 
			t.product.configurations.map(c => cvDouble(COLLISION_ENERGY_ACC, c.cvParams))
				.find(_.nonEmpty).map(_.get)
		val ions = new ArrayBuffer[String]
		for (i <- t.product.interpretations) yield {
			var s = ""
			var o = ""
			for (cv <- i.cvParams)
				cv.accession match {
					case FRAG_Y_SERIES_ACC 	=> s = "y"
					case FRAG_B_SERIES_ACC 	=> s = "b"
					case FRAG_ORDINAL_ACC 	=> o = cv.value.get
					case _ 						=> {}
				}
			if (s != "" && o != "") 
				ions += s+o
			
		}
		
		for (up <- t.userParams.find(_.name == OPENSWATH_UPARAM_ANNOTATION))
			ions += up.value.get
			
		val localRT = 
			t.retentionTime.flatMap(
				ClearRetentionTime.fromRetentionTime
			)
			
		(AssayInfo(q1.get, q1z, ce, localRT), Channel(q3.get, q3z.get, ions.mkString(";"), 2, expIntensity))
	}
	
	def parseChannel(t:Target) = {
		
		val q1 = cvDouble(ISOLATION_WINDOW_TARGET_ACC, t.precursor.cvParams)
		val q1z = cvInt(CHARGE_STATE_ACC, t.precursor.cvParams)
		val expIntensity = cvDouble(PRECURSOR_ION_INTENSITY_ACC, t.cvParams)
		
		val localRT = 
			t.retentionTime.flatMap(
				ClearRetentionTime.fromRetentionTime
			)
			
		(AssayInfo(q1.get, q1z, None, localRT), Channel(q1.get, q1z.get, t.id, 1, expIntensity))
	}
	

	
}


class ClearTraML {

	import ClearTraML._
	
	var traml:TraML = _
	val proteins = new HashSet[String]
	val compounds = new ArrayBuffer[ClearPeptide]
	
	def write(w:XmlWriter) = {
		
		if (traml == null) traml = new TraML
		traml.proteins.clear
		for (t <- proteins)
			traml.proteins += toProtein(t)
		
		if (traml.compoundList == null) traml.compoundList = new CompoundList
		traml.compoundList.peptides.clear
		
		traml.transitions.clear
		val tl = new TargetList
		for {
			p <- compounds
			assay <- p.assays
		} {
			traml.compoundList.peptides += toPeptide(p, assay)
			for (c <- assay.ms1Channels) 
				tl.targetIncludes += toTarget(p, assay, c)
			for (c <- assay.ms2Channels)
				traml.transitions += toTransition(p, assay, c)
		}
		
		if (tl.targetIncludes.nonEmpty)
			traml.targetList = Some(tl)
		traml.write(w)
	}
	
	def assayId(p:ClearPeptide, a:Assay) = p.id + "_" + a.z
	
	def toProtein(id:String) = {
		val p = new Protein
		p.id = id
		p
	}
	
	def toPeptide(c:ClearPeptide, a:Assay) = {
		val p = new Peptide
		p.id = assayId(c, a)
		p.sequence = c.id
		p.userParams += UserParam.string(FULL_PEPTIDE_NAME, Some(c.id))
		p.proteinRefs ++= c.proteins
		c.rt.foreach(rt => p.retentionTimes += rt.toRetentionTime)
		p
	}
	
	def toTarget(p:ClearPeptide, a:Assay, c:Channel) = {
		val t = new Target
		t.id = c.id
		t.cvParams += CvParam.MS(PRECURSOR_ION_INTENSITY_ACC, "precursor ion intensity", c.expIntensity, None)
		t.peptideRef = Some(assayId(p, a))
		t.precursor = new Precursor
		t.precursor.cvParams += CvParam.MS(ISOLATION_WINDOW_TARGET_ACC, "isolation window target m/z", Some(c.mz), None)
		t.precursor.cvParams += CvParam.MS(CHARGE_STATE_ACC, "charge state", Some(c.z), None)
		t
	}
	
	def toTransition(p:ClearPeptide, a:Assay, c:Channel) = {
		val t = new Transition
		t.id = assayId(p, a)+"_"+c.id
		t.cvParams += CvParam.MS(PRODUCT_ION_INTENSITY_ACC, "product ion intensity", c.expIntensity, None)
		t.userParams += UserParam.string("annotation", Some(c.id))
		t.peptideRef = Some(assayId(p, a))
		t.precursor = new Precursor
		t.precursor.cvParams += CvParam.MS(ISOLATION_WINDOW_TARGET_ACC, "isolation window target m/z", Some(a.mz), None)
		t.precursor.cvParams += CvParam.MS(CHARGE_STATE_ACC, "charge state", Some(a.z), None)
		t.product = new Product
		t.product.cvParams += CvParam.MS(ISOLATION_WINDOW_TARGET_ACC, "isolation window target m/z", Some(c.mz), None)
		t.product.cvParams += CvParam.MS(CHARGE_STATE_ACC, "charge state", Some(c.z), None)
		for (ce <- a.ce) {
			val c = new Configuration
			c.cvParams += CvParam.MS(COLLISION_ENERGY_ACC, "collision energy", Some(ce), None)
			t.product.configurations += c
		}
		t
	}
}