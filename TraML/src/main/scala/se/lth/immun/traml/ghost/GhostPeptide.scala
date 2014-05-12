package se.lth.immun.traml.ghost


import se.lth.immun.traml.Peptide
import se.lth.immun.traml.CvParam
import se.lth.immun.traml.UserParam
import scala.collection.mutable.ArrayBuffer

object GhostPeptide {
	
	import Ghost._
	
	val FULL_PEPTIDE_NAME = "full_peptide_name"
	
	def fromPeptide(p:Peptide):GhostPeptide = {
		var x = new GhostPeptide
		
		x.id = p.id
		x.sequence = p.sequence
		x.tramlPeptide = p
		
		for (cv <- p.cvParams)
			cv.accession match {
				case HEAVY_LABELLED_PEPTIDE_ACC => x.label = "heavy"
				case CHARGE_STATE_ACC => 
					if (cv.value.isDefined) 
						x.charge = Some(cv.value.get.toInt)
				case _ => {}
			}
		
		for (up <- p.userParams.find(_.name == "label")) {
			x.label = up.value.get
		}
		
		for (cv <- p.cvParams.find(_.accession == PEPTIDE_GROUP_LABEL)) {
			x.labelGroup = cv.value.get
		}
		
		for (u <- p.userParams)
			u.name match {
				case FULL_PEPTIDE_NAME => x.fullPeptideName = u.value
				case _ => {}
			}
		for (pr <- p.proteinRefs)
			x.proteins += pr
		
		return x
	}
}


class GhostPeptide {
	var id:String 				= null
	var sequence:String 		= null
	var label:String = ""
	var labelGroup:String = ""
	var proteins 				= new ArrayBuffer[String]
	var charge:Option[Int]		= None
	var fullPeptideName:Option[String] = None
	var tramlPeptide:Peptide 	= null
	
	import Ghost._
	
	def toPeptide = {
		var p = new Peptide
		p.id = id
		p.sequence = sequence
		if (label != null && label != "")
			p.userParams.find(_.name == "label") match {
				case Some(up) => up.value = Some(label)
				case None => p.userParams += labelParam
			}
			
		
		if (labelGroup != null && labelGroup != "")
			p.cvParams.find(_.accession == PEPTIDE_GROUP_LABEL) match {
				case Some(cv) => cv.value = Some(labelGroup)
				case None => p.cvParams += labelGroupParam
			}
		
		if (charge.isDefined)
			p.cvParams += chargeParam
		
		if (fullPeptideName.isDefined)
			p.userParams += fullSequenceParam
		
		for (ref <- proteins) p.proteinRefs += ref
		
		if (tramlPeptide != null) {
			p.retentionTimes = tramlPeptide.retentionTimes
			p.modifications = tramlPeptide.modifications
			p.evidence = tramlPeptide.evidence
		}
		p
	}
	
	
	
	def labelParam = {
		var u = new UserParam
		u.name = "label"
		u.dataType = Some("xs:string")
		u.value = Some(label)
		u
	}
	
	
	def labelGroupParam = {
		var cv = new CvParam
		cv.cvRef = "MS"
		cv.accession = PEPTIDE_GROUP_LABEL
		cv.name = "peptide group label"
		cv.value = Some(labelGroup)
		cv
	}
	
	def chargeParam = {
		var cv = new CvParam
		cv.cvRef = "MS"
		cv.accession = CHARGE_STATE_ACC
		cv.name = "charge state"
		cv.value = charge.map(_.toString)
		cv
	}
	def fullSequenceParam = {
		var u = new UserParam
		u.name = GhostPeptide.FULL_PEPTIDE_NAME
		u.value = fullPeptideName
		u.dataType = Some("xsd:string")
		u
	}
}