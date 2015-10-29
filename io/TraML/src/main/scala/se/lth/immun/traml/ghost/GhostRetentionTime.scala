package se.lth.immun.traml.ghost

import se.lth.immun.traml.CvParam
import se.lth.immun.traml.RetentionTime

import Ghost._

object GhostRetentionTime {
	
	
	def fromRetentionTime(rt:RetentionTime) = {
		def rtFind(acc:String) =
			rt.cvParams.find(_.accession == acc)
		
		lazy val irtOpt 	= rtFind(IRT_NORM_STANDARD_ACC)
		lazy val ntimeOpt 	= rtFind(NORMALIZED_RETENTION_TIME_ACC)
		lazy val localOpt 	= rtFind(LOCAL_RETENTION_TIME_ACC)
		
		if (irtOpt.nonEmpty)
			Some(IRT(irtOpt.get.value.get.toDouble))
		else if (ntimeOpt.nonEmpty)
			Some(Ntime(ntimeOpt.get.value.get.toDouble))
		else
			localOpt.map(local => {
				val t = local.value.get.toDouble
				val tLowOff = rtFind(RT_WINDOW_LOWER_OFFSET_ACC).map(_.value.get.toDouble)
				val tHighOff = rtFind(RT_WINDOW_LOWER_OFFSET_ACC).map(_.value.get.toDouble)
				AbsoluteRT(t, tLowOff.map(t - _), tHighOff.map(t + _))
			})
	}
	
	
	trait RT { 
		def t:Double 
		def toRetentionTime:RetentionTime
	}
	case class AbsoluteRT( val t:Double, tStart:Option[Double], tEnd:Option[Double]) extends RT {
		def toRetentionTime = {
			val x = new RetentionTime
			x.cvParams += CvParam.MS(
						LOCAL_RETENTION_TIME_ACC,
						"local retention time",
						Some(t), 
						None)
			
			tStart.foreach(t0 => {
				x.cvParams += CvParam.MS(
								RT_WINDOW_LOWER_OFFSET_ACC,
								"retention time window lower offset",
								Some(t-t0), None)
				
			})
			
			tEnd.foreach(tn => {
				x.cvParams += CvParam.MS(
						RT_WINDOW_UPPER_OFFSET_ACC,
						"retention time window upper offset",
						Some(tn-t), None)
			})
			x
		}
	}
	case class IRT( val t:Double ) extends RT {
		def toRetentionTime = {
			val x = new RetentionTime
			x.cvParams += CvParam.MS(
						IRT_NORM_STANDARD_ACC,
						"iRT retention time normalization standard",
						Some(t), None)
			x
		}
	}
	case class Ntime( val t:Double ) extends RT {
		def toRetentionTime = {
			val x = new RetentionTime
			x.cvParams += CvParam.MS(
						NORMALIZED_RETENTION_TIME_ACC,
						"normalized retention time",
						Some(t), None)
			x
		}
	}
}