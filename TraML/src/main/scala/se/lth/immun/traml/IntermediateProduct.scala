package se.lth.immun.traml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object IntermediateProduct {
	
	import TraML._
	
	def fromFile(r:XmlReader):IntermediateProduct = {
		var x = new IntermediateProduct
		var e = r.top
		
		r.next
		while (r.in(e))
			r.top.name match {
				case CV_PARAM => 
					x.cvParams += CvParam.fromFile(r)
				case USER_PARAM => 
					x.userParams += UserParam.fromFile(r)
				case INTERPRETATION_LIST => {
					r.next
					while (r.was(INTERPRETATION))
						x.interpretations += Interpretation.fromFile(r)
				}
				case CONFIGURATION_LIST => {
					r.next
					while (r.was(CONFIGURATION))
						x.configurations += Configuration.fromFile(r)
				}
				case _ => r.skipThis
			}
		
		return x
	}
}

class IntermediateProduct {
	
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	var interpretations = new ArrayBuffer[Interpretation]
	var configurations = new ArrayBuffer[Configuration]
	
	
	def write(w:XmlWriter) = {
		import TraML._
		
		w.startElement(INTERMEDIATE_PRODUCT)
		
		cvParams.foreach(x => x.write(w))
		userParams.foreach(x => x.write(w))

		if (!interpretations.isEmpty) {
			w.startElement(INTERPRETATION_LIST)
			interpretations.foreach(x => x.write(w))
			w.endElement
		}
		if (!configurations.isEmpty) {
			w.startElement(CONFIGURATION_LIST)
			configurations.foreach(x => x.write(w))
			w.endElement
		}
		w.endElement
	}
}