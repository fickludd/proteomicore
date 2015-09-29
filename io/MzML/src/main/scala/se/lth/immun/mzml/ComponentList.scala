package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object ComponentList {
	
	import MzML._
	
	def fromFile(r:XmlReader) = {
		var x = new ComponentList
		
		var e = r.top
		r.next
		while (r.in(e))
			r.top.name match {
				case SOURCE => 
					x.sources += Source.fromFile(r)
				case ANALYZER => 
					x.analyzers += Analyzer.fromFile(r)
				case DETECTOR => 
					x.detectors += Detector.fromFile(r)
				case _ => r.skipThis
			}
		
		x
	}
}

class ComponentList {
	var sources = new ArrayBuffer[Source]
	var analyzers = new ArrayBuffer[Analyzer]
	var detectors = new ArrayBuffer[Detector]
	
	
	def write(w:XmlWriter) = {
		import MzML._
		
		w.startElement(COMPONENT_LIST)
		w.writeAttribute(COUNT, sources.length + analyzers.length + detectors.length)
		for (x <- sources) x.write(w)
		for (x <- analyzers) x.write(w)
		for (x <- detectors) x.write(w)
		w.endElement
	}
}