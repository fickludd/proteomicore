package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object ReferenceableParamGroup {
	
	import MzML._
	
	def fromFile(r:XmlReader) = {
		var x = new ReferenceableParamGroup
		
		x.id = r.readAttribute(ID)
		
		var e = r.top
		r.next
		while (r.in(e))
			r.top.name match {
				case CV_PARAM => 
					x.cvParams += CvParam.fromFile(r)
				case USER_PARAM => 
					x.userParams += UserParam.fromFile(r)
				case _ => r.skipThis
			}
		
		x
	}
}

class ReferenceableParamGroup {
	var id:String = null
	
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	
	
	def write(w:XmlWriter) = {
		import MzML._
		
		w.startElement(REFERENCEABLE_PARAM_GROUP)
		w.writeAttribute(ID, id)
		for (x <- cvParams) x.write(w)
		for (x <- userParams) x.write(w)
		w.endElement
	}
}