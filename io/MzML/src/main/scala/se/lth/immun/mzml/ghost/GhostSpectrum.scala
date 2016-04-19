package se.lth.immun.mzml.ghost

import se.lth.immun.mzml._
import se.lth.immun.base64.Base64
import java.util.zip.Inflater

object GhostSpectrum {
	
	val MS_LEVEL_ACC = "MS:1000511"
	val BASE_PEAK_INT_ACC = "MS:1000505"
	val BASE_PEAK_MZ_ACC = "MS:1000504"
	val TIC_ACC = "MS:1000285"
	val SCAN_START_TIME_ACC = "MS:1000016"
	val SCAN_WINDOW_MIN_ACC = "MS:1000501"
	val SCAN_WINDOW_MAX_ACC = "MS:1000500"
	val SELECTED_ION_MZ_ACC = "MS:1000744"
	
	def fromSpectrum(s:Spectrum) = {
		var gs = new GhostSpectrum
		
		gs.id = s.id
		gs.spectrum = s
		for (bda <- s.binaryDataArrays)
			gs.readBinaryArray(bda, s.defaultArrayLength)
			
		for (cv <- s.cvParams)
			cv.accession match {
				case MS_LEVEL_ACC => 
					gs.msLevel = cv.value.get.toInt
				case BASE_PEAK_INT_ACC => 
					gs.basePeakIntensity = cv.value.get.toDouble
				case BASE_PEAK_MZ_ACC => 
					gs.basePeakMZ = cv.value.get.toDouble
				case TIC_ACC => 
					gs.totalIonCurrent = cv.value.get.toDouble
				case _ => {}
			}
		
		s.scanList match {
			case Some(sl) => {
				var scan = sl.scans(0)
				for (cv <- scan.cvParams)
					cv.accession match {
						case SCAN_START_TIME_ACC => 
							gs.scanStartTime = cv.value.get.toDouble
						case _ => {}
					}
				for (cv <- scan.scanWindows(0).cvParams)
					cv.accession match {
						case SCAN_WINDOW_MIN_ACC => 
							gs.scanWindowMin = cv.value.get.toDouble
						case SCAN_WINDOW_MAX_ACC => 
							gs.scanWindowMax = cv.value.get.toDouble
						case _ => {}
					}
			} 
			case None => {}
		}
		
		if (!s.precursors.isEmpty)
			if (!s.precursors(0).selectedIons.isEmpty) {
				val si = s.precursors(0).selectedIons(0)
				for (cv <- si.cvParams)
					cv.accession match {
						case SELECTED_ION_MZ_ACC => 
							gs.q1 = cv.value.get.toDouble
						case _ => {} 
					}
			}
		
		gs
	}
}

class GhostSpectrum {
	
	import Ghost._
	
	var id:String 					= null
	var spectrum:Spectrum 			= null
	var mzs:Seq[Double] 			= null
	var intensities:Seq[Double] 	= null
	var dataDef:GhostBinaryDataArray.DataDef = null
	var msLevel 			= 0
	var basePeakIntensity 	= 0.0
	var basePeakMZ 			= 0.0
	var totalIonCurrent		= 0.0
	var scanStartTime		= 0.0
	var scanWindowMin		= 0.0
	var scanWindowMax		= 0.0
	var q1					= 0.0
	
	
	
	def readBinaryArray(b:BinaryDataArray, defaultArrayLength:Int):Unit = {
		val (dataDef, data) = GhostBinaryDataArray.read(b, defaultArrayLength)
		dataDef.dataType match {
			case GhostBinaryDataArray.MZ() =>
				mzs = data
			case GhostBinaryDataArray.Intensity() =>
				intensities = data
			case x =>
				throw new GhostException("Unknown or unsupported datatype in spectrum: "+x)
		}
		this.dataDef = dataDef
		b.binary = ""
	}
}