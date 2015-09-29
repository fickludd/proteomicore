package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer
import java.nio.channels.FileChannel

object SpectrumList {
	
	import MzML._
	
	def fromFile(r:XmlReader, dataHandlers:MzMLDataHandlers, binaryByteChannel:FileChannel) = {
		var x = new SpectrumList
		
		var count = r.readAttributeInt(COUNT)
		x.defaultDataProcessingRef = r.readAttribute(DEFAULT_DATA_PROCESSING_REF)
		
		dataHandlers.specCount(count)
		
		var e = r.top
		r.next
		while (r.in(e))
			r.top.name match {
				case SPECTRUM => {
					var s = Spectrum.fromFile(r, binaryByteChannel)
					dataHandlers.spectrum(s)
				}
				case _ => r.skipThis
			}
		
		x
	}
}

class SpectrumList {
	import MzML._
	
	var defaultDataProcessingRef:String = null
	
	def write(
			w:XmlWriter, 
			specCount:Int, 
			writeSpectra:XmlWriter => Seq[OffsetRef]
	) = {
		
		w.startElement(SPECTRUM_LIST)
		w.writeAttribute(COUNT, specCount)
		w.writeAttribute(DEFAULT_DATA_PROCESSING_REF, defaultDataProcessingRef)
		
		val byteOffsets = writeSpectra(w)
		
		w.endElement
		
		byteOffsets
	}
}