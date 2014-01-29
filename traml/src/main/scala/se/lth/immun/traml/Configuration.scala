package se.lth.immun.traml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object Configuration {
	
	import TraML._
	
	def fromFile(r:XmlReader):Configuration = {
		var x = new Configuration
		var e = r.top
		
		x.instrumentRef = r.readAttribute(INSTRUMENT_REF)
		x.contactRef = r.readOptional(CONTACT_REF)
		
		r.next
		while (r.in(e))
			r.top.name match {
				case CV_PARAM => 
					x.cvParams += CvParam.fromFile(r)
				case USER_PARAM => 
					x.userParams += UserParam.fromFile(r)
				case VALIDATION_STATUS => 
					x.validationStatuses += ValidationStatus.fromFile(r)
				case _ => r.skipThis
			}
		
		return x
	}
}

class Configuration {
	var contactRef:Option[String] = None
	var instrumentRef:String = null

	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	var validationStatuses = new ArrayBuffer[ValidationStatus]
	
	
	def write(w:XmlWriter) = {
		import TraML._
		
		w.startElement(CONFIGURATION)
		w.writeAttribute(INSTRUMENT_REF, instrumentRef)
		w.writeOptional(CONTACT_REF, contactRef)
		
		cvParams.foreach(x => x.write(w))
		userParams.foreach(x => x.write(w))
		validationStatuses.foreach(x => x.write(w))
		w.endElement
	}
}