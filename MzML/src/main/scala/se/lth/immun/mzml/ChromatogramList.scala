package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer
import java.nio.channels.FileChannel

object ChromatogramList {
	
	import MzML._
	
	def fromFile(r:XmlReader, dataHandlers:MzMLDataHandlers, binaryByteChannel:FileChannel) = {
		var x = new ChromatogramList
		
		var count = r.readAttributeInt(COUNT)
		x.defaultDataProcessingRef = r.readAttribute(DEFAULT_DATA_PROCESSING_REF)
		
		dataHandlers.chromCount(count)
		
		var e = r.top
		r.next
		while (r.in(e))
			r.top.name match {
				case CHROMATOGRAM => {
					var c = Chromatogram.fromFile(r, binaryByteChannel)
					dataHandlers.chromatogram(c)
				}
				case _ => { r.skipThis; r.next }
			}
		
		x
	}
}

class ChromatogramList {
	var defaultDataProcessingRef:String = null
	
	def write(
			w:XmlWriter, 
			chromCount:Int, 
			writeChromatogram:XmlWriter => Unit
	) = {
		import MzML._
		
		w.startElement(CHROMATOGRAM_LIST)
		w.writeAttribute(COUNT, chromCount)
		w.writeAttribute(DEFAULT_DATA_PROCESSING_REF, defaultDataProcessingRef)
		
		writeChromatogram(w)
		
		w.endElement
	}
}