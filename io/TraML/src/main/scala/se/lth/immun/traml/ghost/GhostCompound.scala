package se.lth.immun.traml.ghost

import se.lth.immun.traml.Compound
import se.lth.immun.traml.CvParam
import se.lth.immun.traml.UserParam

object GhostCompound {
	
	def fromCompound(c:Compound):GhostCompound = {
		
		import Ghost._
		
		val gc = new GhostCompound
		gc.compound = c
		
		gc.id = c.id
		
		for (up <- c.userParams.find(_.name == "label")) {
			gc.label = up.value.get
		}
		
		for (cv <- c.cvParams.find(_.accession == PEPTIDE_GROUP_LABEL)) {
			gc.labelGroup = cv.value.get
		}
		
		gc.preferredCharges = 
			c.cvParams.filter(_.accession == CHARGE).map(_.value.get.toInt)
		
		return gc
	}
}

class GhostCompound {

	import Ghost._
	
	var id:String = _
	var mass:Double = _
	var compound:Compound = _
	var label:String = ""
	var labelGroup:String = ""
	var preferredCharges:Seq[Int] = Nil
	
	def toCompound = {
		if (compound == null)
			compound = new Compound
		
		if (id != null)
			compound.id = id
		
		if (label != null && label != "")
			compound.userParams.find(_.name == "label") match {
				case Some(up) => up.value = Some(label)
				case None => compound.userParams += labelParam
			}
			
		
		if (labelGroup != null && labelGroup != "")
			compound.cvParams.find(_.accession == PEPTIDE_GROUP_LABEL) match {
				case Some(cv) => cv.value = Some(labelGroup)
				case None => compound.cvParams += labelGroupParam
			}
		
		compound.cvParams = compound.cvParams.filter(_.accession != CHARGE)
		for (pf <- preferredCharges)
			compound.cvParams += chargeParam(pf)
		
		compound
	}
	
	
	def chargeParam(c:Int) = {
		var cv = new CvParam
		cv.cvRef = "MS"
		cv.accession = CHARGE
		cv.name = "charge state"
		cv.value = Some(c.toString)
		cv
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
}