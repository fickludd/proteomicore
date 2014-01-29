package se.lth.immun.traml.ghost


import se.lth.immun.traml.TraML
import se.lth.immun.traml.Protein
import se.lth.immun.traml.CvParam

object GhostProtein {
	
	import Ghost._
	
	def fromProtein(p:Protein):GhostProtein = {
		var x = new GhostProtein
		
		x.id = p.id
		for (cv <- p.cvParams)
			cv.accession match {
				case PROTEIN_ACCESSION_ACC => x.accession = cv.value.get
				case PROTEIN_SHORT_NAME_ACC => x.shortName = cv.value.get
				case PROTEIN_NAME_ACC => x.name = cv.value.get
				case _ => {}
			}
		x.protein = p
		
		return x
	}
}

class GhostProtein {
	var id:String = null
	var name:String = null
	var shortName:String = null
	var sequence:String = null
	var accession:String = null
	var protein:Protein = null
	
	import Ghost._
	
	def toProtein = {
		var p = if (protein != null) protein else new Protein
		p.id = id
		p.sequence = sequence
		p.cvParams.find(_.accession == PROTEIN_ACCESSION_ACC) match {
			case Some(cv) => {}
			case None => p.cvParams += accessionParam
		}
		p.cvParams.find(_.accession == PROTEIN_SHORT_NAME_ACC) match {
			case Some(cv) => {}
			case None => p.cvParams += shortNameParam
		}
		p.cvParams.find(_.accession == PROTEIN_NAME_ACC) match {
			case Some(cv) => {}
			case None => p.cvParams += nameParam
		}
		p
	}
	
	
	def accessionParam = {
		var cv = new CvParam
		cv.cvRef = "MS"
		cv.accession = PROTEIN_ACCESSION_ACC
		cv.name = "protein accession"
		cv.value = Some(accession)
		cv
	}
	
	
	def shortNameParam = {
		var cv = new CvParam
		cv.cvRef = "MS"
		cv.accession = PROTEIN_SHORT_NAME_ACC
		cv.name = "protein short name"
		cv.value = Some(shortName)
		cv
	}
	
	
	def nameParam = {
		var cv = new CvParam
		cv.cvRef = "MS"
		cv.accession = PROTEIN_NAME_ACC
		cv.name = "protein name"
		cv.value = Some(name)
		cv
	}
}