package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object ProcessingMethod {
	
	import MzML._
	
	def fromFile(r:XmlReader) = {
		var x = new ProcessingMethod
		
		x.order = r.readAttributeInt(ORDER)
		x.softwareRef = r.readAttribute(SOFTWARE_REF)
		
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
				case _ => r.skipThis
			}
		
		x
	}
}

class ProcessingMethod {
	var order:Int = -1
	var softwareRef:String = null
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	var paramGroupRefs = new ArrayBuffer[String]
	
	
	def write(w:XmlWriter) = {
		import MzML._
		
		w.startElement(PROCESSING_METHOD)
		w.writeAttribute(ORDER, order)
		w.writeAttribute(SOFTWARE_REF, softwareRef)
		for (x <- paramGroupRefs) ReferenceableParamGroupRef.write(w, x)
		for (x <- cvParams) x.write(w)
		for (x <- userParams) x.write(w)
		w.endElement
	}
}