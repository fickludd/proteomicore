package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer
import java.nio.channels.FileChannel

object Run {
	
	import MzML._
	
	def fromFile(r:XmlReader, dataHandlers:MzMLDataHandlers, binaryByteChannel:FileChannel) = {
		var x = new Run
		
		x.id = r.readAttribute(ID)
		x.defaultInstrumentConfigurationRef = r.readAttribute(DEFAULT_INSTRUMENT_CONFIGURATION_REF)
		x.defaultSourceFileRef = r.readOptional(DEFAULT_SOURCE_FILE_REF)
		x.sampleRef = r.readOptional(SAMPLE_REF)
		x.startTimeStamp = r.readOptional(START_TIMESTAMP)
		
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
				case SPECTRUM_LIST => 
					x.spectrumList = Some(SpectrumList.fromFile(r, dataHandlers, binaryByteChannel))
				case CHROMATOGRAM_LIST => 
					x.chromatogramList = Some(ChromatogramList.fromFile(r, dataHandlers, binaryByteChannel))
				case _ => {r.skipThis; r.next}
			}
		
		x
	}
}

class Run {
	import MzML._
	
	var id:String = null
	var defaultInstrumentConfigurationRef:String = null
	var defaultSourceFileRef:Option[String] = None
	var sampleRef:Option[String] = None
	var startTimeStamp:Option[String] = None
	
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	var paramGroupRefs = new ArrayBuffer[String]
	
	var spectrumList:Option[SpectrumList] = None
	var chromatogramList:Option[ChromatogramList] = None
	
	
	def write(w:XmlWriter, dw:MzMLDataWriters):(Option[Seq[OffsetRef]], Option[Seq[OffsetRef]]) = {
		
		w.startElement(RUN)
		w.writeAttribute(ID, id)
		w.writeAttribute(DEFAULT_INSTRUMENT_CONFIGURATION_REF, defaultInstrumentConfigurationRef)
		w.writeOptional(SAMPLE_REF, sampleRef)
		w.writeOptional(START_TIMESTAMP, startTimeStamp)
		w.writeOptional(DEFAULT_SOURCE_FILE_REF, defaultSourceFileRef)
		for (x <- paramGroupRefs) ReferenceableParamGroupRef.write(w, x)
		for (x <- cvParams) x.write(w)
		for (x <- userParams) x.write(w)
		
		if (spectrumList.isEmpty && dw.specCount > 0) 
			throw new Exception("Cannot write spectra as mzml.run.spectrumList is undefined!")
		val spectrumIndices = spectrumList.map(_.write(w, dw.specCount, dw.writeSpectra))
			
		if (chromatogramList.isEmpty && dw.chromCount > 0)
			throw new Exception("Cannot write chromatograms as mzml.run.chromatogramList is undefined!")
		val chromatogramIndices = chromatogramList.map(_.write(w, dw.chromCount, dw.writeChromatograms))
		
		w.endElement
		
		(spectrumIndices, chromatogramIndices)
	}
}