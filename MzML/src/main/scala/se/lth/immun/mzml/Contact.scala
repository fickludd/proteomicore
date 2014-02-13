package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object Contact {
	
	import MzML._
	
	def fromFile(r:XmlReader):Contact = {
		var x = new Contact
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
		
		return x
	}
}

class Contact {
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	var paramGroupRefs = new ArrayBuffer[String]
	
	
	def write(w:XmlWriter) = {
		import MzML._
		
		w.startElement(CONTACT)
		cvParams.foreach(x => x.write(w))
		userParams.foreach(x => x.write(w))
		w.endElement
	}
}