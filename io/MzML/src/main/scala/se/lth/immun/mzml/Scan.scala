package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object Scan {
	
	import MzML._
	
	def fromFile(r:XmlReader) = {
		var x = new Scan
		
		x.externalSpectrumID = r.readOptional(EXTERNAL_SPECTRUM_ID)
		x.instrumentConfigurationRef = r.readOptional(INSTRUMENT_CONFIGURATION_REF)
		x.sourceFileRef = r.readOptional(SOURCE_FILE_REF)
		x.spectrumRef = r.readOptional(SPECTRUM_REF)
		
		var e = r.top
		r.next
		while (r.in(e))
			r.top.name match {
				case CV_PARAM => 
					x.cvParams += CvParam.fromFile(r)
				case USER_PARAM => 
					x.userParams += UserParam.fromFile(r)
				case REFERENCEABLE_PARAM_GROUP_REF => {
					x.paramGroupRefs += r.readAttribute(REF)
					r.next
				}
				case SCAN_WINDOW_LIST => {
					r.next
					while (r.was(SCAN_WINDOW))
						x.scanWindows += ScanWindow.fromFile(r)
				}
				case _ => r.skipThis
			}
		
		x
	}
}

class Scan {
	var externalSpectrumID:Option[String] = None
	var instrumentConfigurationRef:Option[String] = None
	var sourceFileRef:Option[String] = None
	var spectrumRef:Option[String] = None
	
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	var paramGroupRefs = new ArrayBuffer[String]
	
	var scanWindows = new ArrayBuffer[ScanWindow]
	
	
	def write(w:XmlWriter) = {
		import MzML._
		
		w.startElement(SCAN)
		w.writeOptional(EXTERNAL_SPECTRUM_ID, externalSpectrumID)
		w.writeOptional(INSTRUMENT_CONFIGURATION_REF, instrumentConfigurationRef)
		w.writeOptional(SOURCE_FILE_REF, sourceFileRef)
		w.writeOptional(SPECTRUM_REF, spectrumRef)
		for (x <- paramGroupRefs) ReferenceableParamGroupRef.write(w, x)
		for (x <- cvParams) x.write(w)
		for (x <- userParams) x.write(w)
		
		if (!scanWindows.isEmpty) {
			w.startListElement(SCAN_WINDOW_LIST, scanWindows)
			for (x <- scanWindows) x.write(w)
			w.endElement
		}
		
		w.endElement
	}
}