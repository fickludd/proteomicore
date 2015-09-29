package se.lth.immun.traml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object Prediction {
	
	import TraML._
	
	def fromFile(r:XmlReader):Prediction = {
		var x = new Prediction
		var e = r.top
		
		x.softwareRef = r.readAttribute(SOFTWARE_REF)
		x.contactRef = r.readOptional(CONTACT_REF)

		r.next
		while (r.in(e))
			r.top.name match {
				case CV_PARAM => 
					x.cvParams += CvParam.fromFile(r)
				case USER_PARAM => 
					x.userParams += UserParam.fromFile(r)
				case _ => r.skipThis
			}
		
		return x
	}
}

class Prediction {
	var contactRef:Option[String] = None
	var softwareRef:String = null
	
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	
	
	def write(w:XmlWriter) = {
		import TraML._
		
		w.startElement(PREDICTION)
		w.writeAttribute(SOFTWARE_REF, softwareRef)
		w.writeOptional(CONTACT_REF, contactRef)
		
		cvParams.foreach(x => x.write(w))
		userParams.foreach(x => x.write(w))
		w.endElement
	}
} 