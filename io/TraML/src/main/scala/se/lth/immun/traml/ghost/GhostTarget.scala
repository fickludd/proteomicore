package se.lth.immun.traml.ghost

import se.lth.immun.traml.Target
import se.lth.immun.traml.CvParam
import se.lth.immun.traml.Precursor
import se.lth.immun.traml.Configuration
import se.lth.immun.traml.RetentionTime

import Ghost._
	
object GhostTarget {

	def fromTarget(
			t:Target,
			peps:String => GhostPeptide, 
			comps:String => GhostCompound
	):GhostTarget = {
		var x = new GhostTarget
		
		x.id = t.id
		
		x.intensity = t.cvParams.find(
				_.accession == PRECURSOR_ION_INTENSITY_ACC
			).flatMap(_.value.map(_.toDouble))
		
		for (cv <- t.precursor.cvParams)
			cv.accession match {
				case ISOLATION_WINDOW_TARGET_ACC 	=> x.q1 = cv.value.get.toDouble
				case CHARGE_STATE_ACC 				=> x.q1z = cv.value.get.toInt
				case _ 								=> {}
			}
		
		
		for {
			rt <- t.retentionTime
			grt <- GhostRetentionTime.fromRetentionTime(rt)
		} {
			x.localRT = Some(grt)
		}
		
		x.peptide 	= t.peptideRef.map(peps)
		x.compound 	= t.compoundRef.map(comps)
		x.target 	= t
		
		x
	}
}

class GhostTarget {
	var id:String = null
	
	var q1:Double = -1
	var q1z:Int = 0
	var localRT:Option[GhostRetentionTime.RT] = None
	var peptide:Option[GhostPeptide] = None
	var compound:Option[GhostCompound] = None
	var intensity:Option[Double] = None
	var target:Target = null
	
	def pepCompId = 
		peptide.map(_.id).getOrElse("") + "-" + compound.map(_.id).getOrElse("") 
		
	def isCompound(id:String) = compound.map(_.id == id).getOrElse(false)
	def isPeptide(id:String) = peptide.map(_.id == id).getOrElse(false)
		
	def toTarget = {
		
		var t = if (target != null) target else new Target
		t.id = id
		
		for (x <- intensity)
			t.cvParams.find(_.accession == PRECURSOR_ION_INTENSITY_ACC) match {
				case Some(cv) => cv.value = Some(intensity.toString)
				case None => t.cvParams += intensityParam
			}
		
		if (t.precursor == null) t.precursor = new Precursor
		t.precursor.cvParams.find(_.accession == ISOLATION_WINDOW_TARGET_ACC) match {
			case Some(cv) => cv.value = Some(q1.toString)
			case None => t.precursor.cvParams += qParam(q1)
		}
		if (q1z > 0) {
			t.precursor.cvParams.find(_.accession == CHARGE_STATE_ACC) match {
				case Some(cv) => cv.value = Some(q1z.toString)
				case None => t.precursor.cvParams += zParam(q1z)
			}
		}
		
		t.retentionTime = localRT.map(_.toRetentionTime)
		
		t.peptideRef = peptide.map(_.id)
		t.compoundRef = compound.map(_.id)
		
		t
	}
	
	
	def intensityParam = 
		CvParam.MS(
			PRECURSOR_ION_INTENSITY_ACC,
			"intensity of precursor ion",
			intensity, None)
	
	
	
	
	def qParam(q:Double) = 
		CvParam.MS(
				ISOLATION_WINDOW_TARGET_ACC,
				"isolation window target m/z",
				Some(q), None)
	
	
	
	def zParam(z:Int) = 
		CvParam.MS(
				CHARGE_STATE_ACC,
				"charge state",
				Some(z), None)
}