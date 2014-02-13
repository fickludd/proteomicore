package se.lth.immun.traml.ghost

import scala.collection.mutable.ArrayBuffer

import se.lth.immun.traml.Transition
import se.lth.immun.traml.CvParam
import se.lth.immun.traml.Precursor
import se.lth.immun.traml.Product
import se.lth.immun.traml.Configuration
import se.lth.immun.traml.Interpretation
import se.lth.immun.traml.RetentionTime

object GhostTransition {

	import Ghost._
	
	def fromTransition(t:Transition):GhostTransition = {
		var x = new GhostTransition
		
		x.id = t.id
		
		for (cv <- t.cvParams)
			cv.accession match {
				case PRODUCT_ION_INTENSITY_ACC 		=> x.intensity = cv.value.get.toDouble
				case _ 								=> {}
			}
		
		for (cv <- t.precursor.cvParams)
			cv.accession match {
				case ISOLATION_WINDOW_TARGET_ACC 	=> x.q1 = cv.value.get.toDouble
				case CHARGE_STATE_ACC 				=> x.q1z = cv.value.get.toInt
				case _ 								=> {}
			}
		
		for (cv <- t.product.cvParams)
			cv.accession match {
				case PRODUCT_ION_INTENSITY_ACC 		=> x.intensity = cv.value.get.toDouble
				case ISOLATION_WINDOW_TARGET_ACC 	=> x.q3 = cv.value.get.toDouble
				case CHARGE_STATE_ACC 				=> x.q3z = cv.value.get.toInt
				case _ 								=> {}
			}
		
		for (conf <- t.product.configurations)
			for (cv <- conf.cvParams)
				cv.accession match {
					case COLLISION_ENERGY_ACC 	=> {
						x.ce = cv.value.get.toDouble
						x.instrumentRef = conf.instrumentRef
					}
					case _ 						=> {}
				}
		
		for(i <- t.product.interpretations)
			for (cv <- i.cvParams) {
				var s = ""
				var o = ""
				cv.accession match {
					case FRAG_Y_SERIES_ACC 	=> s = "y"
					case FRAG_B_SERIES_ACC 	=> s = "b"
					case FRAG_ORDINAL_ACC 	=> o = cv.value.get
					case _ 						=> {}
				}
				if (s != "" && o != "")
					x.ions += s+o
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
		x.transition 	= t
		
		return x
	}
}

class GhostTransition {
	var q1:Double = -1
	var q1z:Int = 0
	var q3:Double = -1
	var q3z:Int = 0
	var ce:Double = -1
	var ions = new ArrayBuffer[String]
	var peptideRef:String = null
	var compoundRef:String = null
	var instrumentRef:String = null
	var rtStart:Double = Double.NaN
	var rtEnd:Double = Double.NaN
	var intensity:Double = -1
	var transition:Transition = null
	var id:String = null
	
	import Ghost._
	
	def toTransition = {
		
		var t = if (transition != null) transition else new Transition
		t.id = id
		
		if (intensity >= 0)
			t.cvParams.find(_.accession == PRODUCT_ION_INTENSITY_ACC) match {
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
		
		if (t.product == null) t.product = new Product
		t.product.cvParams.find(_.accession == ISOLATION_WINDOW_TARGET_ACC) match {
			case Some(cv) => cv.value = Some(q3.toString)
			case None => t.product.cvParams += qParam(q3)
		}
		if (q3z > 0) {
			t.product.cvParams.find(_.accession == CHARGE_STATE_ACC) match {
				case Some(cv) => cv.value = Some(q3z.toString)
				case None => t.product.cvParams += zParam(q3z)
			}
		}
		t.product.cvParams.find(_.accession == PRODUCT_ION_INTENSITY_ACC) match {
			case Some(cv) => t.product.cvParams -= cv
			case None => {}
		}
	
		if (!t.product.configurations.exists(
				_.cvParams.find(_.accession == COLLISION_ENERGY_ACC) match {
					case Some(cv) => {
						cv.value = Some(ce.toString)
						true
					}
					case None => false
				}))
			t.product.configurations += ceConfiguration(instrumentRef)
			
		t.product.interpretations.clear
		for (ion <- ions) t.product.interpretations += interp(ion)
		
		if (!java.lang.Double.isNaN(rtStart) && !java.lang.Double.isNaN(rtEnd))
			t.retentionTime = Some(rt)
		
		
		if (peptideRef != null) t.peptideRef = Some(peptideRef)
		if (compoundRef != null) t.compoundRef = Some(compoundRef)
		
		t
	}
	
	
	
	def intensityParam = {
		var cv = new CvParam
		cv.cvRef = "MS"
		cv.accession = PRODUCT_ION_INTENSITY_ACC
		cv.name = "product ion intensity"
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
	
	
	
	def ceConfiguration(instrumentRef:String) = {
		var cv = new CvParam
		cv.cvRef = "MS"
		cv.accession = COLLISION_ENERGY_ACC
		cv.name = "collision energy"
		cv.value = Some(ce.toString)
		var c = new Configuration
		c.cvParams += cv
		c.instrumentRef = instrumentRef
		c
	}
	
	
	
	def interp(ion:String) = {
		var i = new Interpretation
		var cv = new CvParam
		cv.cvRef = "MS"
		if (ion.startsWith("y")) {
			cv.accession = FRAG_Y_SERIES_ACC
			cv.name = "frag: y ion"
		} else if (ion.startsWith("b")) {
			cv.accession = FRAG_B_SERIES_ACC
			cv.name = "frag: b ion - H2O"
		}
		i.cvParams += cv
		
		cv = new CvParam
		cv.cvRef = "MS"
		cv.accession = FRAG_ORDINAL_ACC
		cv.name = "product ion series ordinal"
		cv.value = Some(ion.tail)
		i.cvParams += cv
		i
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
	
	
	
	override def equals(o:Any) = 
		o match {
			case gt:GhostTransition => 
				q1 == gt.q1 && q3 == gt.q3 && ce == gt.ce
			case _ => false
		}
	
	override def hashCode = q1.hashCode + q3.hashCode + ce.hashCode 
}
