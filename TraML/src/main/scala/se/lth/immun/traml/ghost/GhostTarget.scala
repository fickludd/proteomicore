package se.lth.immun.traml.ghost

import se.lth.immun.traml.Target
import se.lth.immun.traml.CvParam
import se.lth.immun.traml.Precursor
import se.lth.immun.traml.Configuration
import se.lth.immun.traml.RetentionTime

object GhostTarget {

	import Ghost._
	
	def fromTarget(t:Target):GhostTarget = {
		var x = new GhostTarget
		
		x.id = t.id
		
		for (cv <- t.cvParams)
			cv.accession match {
				case PRECURSOR_ION_INTENSITY_ACC 	=> x.intensity = cv.value.get.toDouble
				case _ 								=> {}
			}
		
		for (cv <- t.precursor.cvParams)
			cv.accession match {
				case ISOLATION_WINDOW_TARGET_ACC 	=> x.q1 = cv.value.get.toDouble
				case CHARGE_STATE_ACC 				=> x.q1z = cv.value.get.toInt
				case _ 								=> {}
			}
		
		var rt0 = -1.0
		var rt1 = -1.0
		var rt2 = -1.0
		if (t.retentionTime.isDefined)
			for (cv <- t.retentionTime.get.cvParams)
				cv.accession match {
					case LOCAL_RETENTION_TIME_ACC 		=> rt1 = cv.value.get.toDouble
					case RT_WINDOW_LOWER_OFFSET_ACC 	=> rt0 = cv.value.get.toDouble
					case RT_WINDOW_UPPER_OFFSET_ACC 	=> rt2 = cv.value.get.toDouble
					case _ => {}
				}
		x.rtStart 		= rt1 - rt0
		x.rtEnd 		= rt1 + rt2
		
		if (t.peptideRef.isDefined)
			x.peptideRef = t.peptideRef.get
		if (t.compoundRef.isDefined)
			x.compoundRef = t.compoundRef.get
		x.target 	= t
		
		x
	}
}

class GhostTarget {
	var id:String = null
	
	var q1:Double = -1
	var q1z:Int = 0
	var rtStart:Double = Double.NaN
	var rtEnd:Double = Double.NaN
	var peptideRef:String = null
	var compoundRef:String = null
	var intensity:Double = -1
	var target:Target = null
	
	import Ghost._
	
	def toTarget = {
		
		var t = if (target != null) target else new Target
		t.id = id
		
		if (intensity >= 0)
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
		
		if (!java.lang.Double.isNaN(rtStart) && !java.lang.Double.isNaN(rtEnd))
			t.retentionTime = Some(rt)
		
		
		if (peptideRef != null) t.peptideRef = Some(peptideRef)
		if (compoundRef != null) t.compoundRef = Some(compoundRef)
		
		t
	}
	
	
	def intensityParam = {
		var cv = new CvParam
		cv.cvRef = "MS"
		cv.accession = PRECURSOR_ION_INTENSITY_ACC
		cv.name = "intensity of precursor ion"
		cv.value = Some(intensity.toString)
		cv
	}
	
	
	
	def qParam(q:Double) = {
		var cv = new CvParam
		cv.cvRef = "MS"
		cv.accession = ISOLATION_WINDOW_TARGET_ACC
		cv.name = "isolation window target m/z"
		cv.value = Some(q.toString)
		cv
	}
	
	
	
	def zParam(z:Int) = {
		var cv = new CvParam
		cv.cvRef = "MS"
		cv.accession = CHARGE_STATE_ACC
		cv.name = "charge state"
		cv.value = Some(z.toString)
		cv
	}
	
	
	
	def rt = {
		var x = new RetentionTime
		
		var mid = (rtEnd + rtStart)/2
		var offset = rtEnd - mid
		
		var cv = new CvParam
		cv.cvRef = "MS"
		cv.accession = LOCAL_RETENTION_TIME_ACC
		cv.name = "local retention time"
		cv.value = Some(mid.toString)
		x.cvParams += cv
		
		cv = new CvParam
		cv.cvRef = "MS"
		cv.accession = RT_WINDOW_LOWER_OFFSET_ACC
		cv.name = "retention time window lower offset"
		cv.value = Some(offset.toString)
		x.cvParams += cv
		
		cv = new CvParam
		cv.cvRef = "MS"
		cv.accession = RT_WINDOW_UPPER_OFFSET_ACC
		cv.name = "retention time window upper offset"
		cv.value = Some(offset.toString)
		x.cvParams += cv
		
		x
	}
}