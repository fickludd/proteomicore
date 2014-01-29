package se.lth.immun.traml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object RetentionTime {
	
	import TraML._
	
	def fromFile(r:XmlReader):RetentionTime = {
		var x = new RetentionTime
		var e = r.top
		
		if (r.hasAttribute(SOFTWARE_REF))
			x.softwareRef = Some(r.readAttribute(SOFTWARE_REF))

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

class RetentionTime {
	var softwareRef:Option[String] = None
	
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	
	
	def write(w:XmlWriter) = {
		import TraML._
		
		w.startElement(RETENTION_TIME)
		w.writeOptional(SOFTWARE_REF, softwareRef)
		
		cvParams.foreach(x => x.write(w))
		userParams.foreach(x => x.write(w))
		w.endElement
	}
}