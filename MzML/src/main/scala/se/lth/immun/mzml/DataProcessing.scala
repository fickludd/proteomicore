package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object DataProcessing {
	
	import MzML._
	
	def fromFile(r:XmlReader) = {
		var x = new DataProcessing
		
		x.id = r.readAttribute(ID)
		
		var e = r.top
		r.next
		while (r.in(e))
			r.top.name match {
				case PROCESSING_METHOD => 
					x.processingMethods += ProcessingMethod.fromFile(r)
				case _ => r.skipThis
			}
		
		x
	}
}

class DataProcessing {
	var id:String = null
	var processingMethods = new ArrayBuffer[ProcessingMethod]
	
	
	def write(w:XmlWriter) = {
		import MzML._
		
		w.startElement(DATA_PROCESSING)
		w.writeAttribute(ID, id)
		
		for (x <- processingMethods) x.write(w)
		
		w.endElement
	}
}