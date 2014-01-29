package se.lth.immun.traml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object Compound {
	
	import TraML._
	
	def fromFile(r:XmlReader):Compound = {
		var x = new Compound
		var e = r.top
		
		x.id = r.readAttribute(ID)
		
		r.next
		while (r.in(e))
			r.top.name match {
				case CV_PARAM => 
					x.cvParams += CvParam.fromFile(r)
				case USER_PARAM => 
					x.userParams += UserParam.fromFile(r)
				case RETENTION_TIME_LIST => {
					r.next
					while (r.was(RETENTION_TIME))
						x.retentionTimes += RetentionTime.fromFile(r)
				}
				case _ => r.skipThis
			}
		
		return x
	}
}

class Compound {
	var id:String = null
	
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	var retentionTimes = new ArrayBuffer[RetentionTime]
	
	
	def write(w:XmlWriter) = {
		import TraML._
		
		w.startElement(COMPOUND)
		w.writeAttribute(ID, id)
		
		cvParams.foreach(x => x.write(w))
		userParams.foreach(x => x.write(w))
		
		w.startElement(RETENTION_TIME_LIST)
		retentionTimes.foreach(_.write(w))
		w.endElement
		
		w.endElement
	}
}