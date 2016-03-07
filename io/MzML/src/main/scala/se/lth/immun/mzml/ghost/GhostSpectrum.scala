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
	val ISOLATION_WINDOW_TARGET_ACC = "MS:1000827"
	val ISOLATION_WINDOW_LOW_OFFSET_ACC = "MS:1000828"
	val ISOLATION_WINDOW_HIGH_OFFSET_ACC = "MS:1000829"
	
	case class IsolationWindow(low:Double, high:Double)		
		
	trait MSLevel
	case object MS1 extends MSLevel
	case class MS2(precursorMz:Double, isoWindow:Option[IsolationWindow]) extends MSLevel
		
	def fromSpectrum(s:Spectrum) = {
		var gs = new GhostSpectrum
		
		gs.id = s.id
		gs.spectrum = s
		for (bda <- s.binaryDataArrays)
			gs.readBinaryArray(bda, s.defaultArrayLength)
			
		var annotMsLevel = 0
		for (cv <- s.cvParams)
			cv.accession match {
				case MS_LEVEL_ACC => 
					annotMsLevel = cv.value.get.toInt
				case BASE_PEAK_INT_ACC => 
					gs.basePeakIntensity = cv.value.get.toDouble
				case BASE_PEAK_MZ_ACC => 
					gs.basePeakMZ = cv.value.get.toDouble
				case TIC_ACC => 
					gs.totalIonCurrent = cv.value.get.toDouble
				case _ => {}
			}
		
		
		if (s.scanList.isEmpty || s.scanList.get.scans.isEmpty)
			throw new Exception("Scan meta data missing for spectrum %d: mzML parsing is not supported by the ghost help library".format(s.index))
		
		if (s.scanList.get.scans.length > 1)
			throw new Exception("More than one scan (%d) for spectrum %d: mzML parsing is not supported by the ghost help library".format(s.scanList.get.scans.length, s.index))
		
		for { 
			sl <- s.scanList
			scan <- sl.scans
		} {
			for (cv <- scan.cvParams)
				cv.accession match {
					case SCAN_START_TIME_ACC => 
						gs.scanStartTime = cv.value.get.toDouble
					case _ => {}
				}
			
			for {
				sw <- scan.scanWindows
				cv <- sw.cvParams
			}
				cv.accession match {
					case SCAN_WINDOW_MIN_ACC => 
						gs.scanMzMin = Some(cv.value.get.toDouble)
					case SCAN_WINDOW_MAX_ACC => 
						gs.scanMzMax = Some(cv.value.get.toDouble)
					case _ => {} 
				}
		}
		
		var isoTarget:Option[Double] 	= None
		var isoLowOff:Option[Double] 	= None
		var isoHighOff:Option[Double]	= None
		
		s.precursors.length match {
			case 0 => {}
			case 1 => 
				val p = s.precursors.head
				for {
					iw <- p.isolationWindow
					cv <- iw.cvParams
				} {
					cv.accession match {
						case ISOLATION_WINDOW_TARGET_ACC => 
							isoTarget = Some(cv.value.get.toDouble)
						case ISOLATION_WINDOW_LOW_OFFSET_ACC => 
							isoLowOff = Some(cv.value.get.toDouble)
						case ISOLATION_WINDOW_HIGH_OFFSET_ACC => 
							isoHighOff = Some(cv.value.get.toDouble)
						case _ => {}
					}				
				}
				
				for {
					si <- p.selectedIons
					cv <- si.cvParams
				} {
					cv.accession match {
						case SELECTED_ION_MZ_ACC => 
							isoTarget = Some(cv.value.get.toDouble)
						case _ => {}
					}				
				}
			
			case n => 
				throw new Exception("Spectrum %d: mzML parsing of multiple precursor spectra is not supported by the ghost help library".format(s.index))
		}
		
		isoTarget match { 
			case None => 
				if (annotMsLevel >= 2)
					throw new Exception("No precursor detected for MS2 spectrum %d".format(s.index))
			case Some(precMz) =>// MS2 spectrum
				if (annotMsLevel == 1 && annotMsLevel > 2)
					throw new Exception("Precursor detected for annotated MS1 spectrum %d".format(s.index))
				if (isoLowOff.isDefined && isoHighOff.isDefined)
					gs.msLevel = MS2(precMz, Some(IsolationWindow(precMz - isoLowOff.get, precMz + isoHighOff.get)))
				else
					gs.msLevel = MS2(precMz, None)
					//throw new Exception("Unable to parse isolation window bounds for MS2 spectrum %d".format(s.index))
		}
		
		gs
	}
}

class GhostSpectrum {
	
	import Ghost._
	import GhostSpectrum._
	
	var id:String 					= null
	var spectrum:Spectrum 			= null
	var mzs:Seq[Double] 			= null
	var intensities:Seq[Double] 	= null
	var dataDef:GhostBinaryDataArray.DataDef = null
	var msLevel:MSLevel 	= MS1
	var basePeakIntensity 	= 0.0
	var basePeakMZ 			= 0.0
	var totalIonCurrent		= 0.0
	var scanStartTime		= 0.0
	var scanMzMin:Option[Double] = None
	var scanMzMax:Option[Double] = None
	
	
	
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