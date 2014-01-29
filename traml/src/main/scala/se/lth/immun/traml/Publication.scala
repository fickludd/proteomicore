package se.lth.immun.traml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object Publication {
	
	import TraML._
	
	def fromFile(r:XmlReader):Publication = {
		var x = new Publication
		var e = r.top
		
		x.id = r.readAttribute(ID)

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

class Publication {
	var id:String = null
	
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	
	
	def write(w:XmlWriter) = {
		import TraML._
		
		w.startElement(PUBLICATION)
		w.writeAttribute(ID, id)
		
		cvParams.foreach(x => x.write(w))
		userParams.foreach(x => x.write(w))
		w.endElement
	}
}