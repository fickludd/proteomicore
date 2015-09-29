package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object SourceFile {
	
	import MzML._
	
	def fromFile(r:XmlReader) = {
		var sf = new SourceFile
		var e = r.top
		
		sf.location = r.readAttribute(LOCATION)
		sf.id = r.readAttribute(ID)
		sf.name = r.readAttribute(NAME)
		
		r.next
		while (r.in(e))
			r.top.name match {
				case CV_PARAM => 
					sf.cvParams += CvParam.fromFile(r)
				case USER_PARAM => 
					sf.userParams += UserParam.fromFile(r)
				case REFERENCEABLE_PARAM_GROUP_REF => {
					sf.paramGroupRefs += r.readAttribute(REF)
					r.next
				}
				case _ => r.skipThis
			}
		
		sf
	}
}

class SourceFile {

	var id:String = null
	var location:String = null
	var name:String = null
	
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	var paramGroupRefs = new ArrayBuffer[String]
	
	
	def write(w:XmlWriter) = {
		import MzML._
		
		w.startElement(SOURCE_FILE)
		w.writeAttribute(ID, id)
		w.writeAttribute(NAME, name)
		w.writeAttribute(LOCATION, location)
		
		cvParams.foreach(x => x.write(w))
		userParams.foreach(x => x.write(w))
		w.endElement
	}
}