package se.lth.immun.traml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object UserParam {
	
	import TraML._
	
	def fromFile(r:XmlReader):UserParam = {
		var x = new UserParam
		
		x.name 			= r.readAttribute(NAME)
		x.dataType 		= r.readOptional(TYPE)
		x.unitAccession = r.readOptional(UNIT_ACCESSION)
		x.unitCvRef 	= r.readOptional(UNIT_CV_REF)
		x.unitName 		= r.readOptional(UNIT_NAME)
		x.value 		= r.readOptional(VALUE)
	
		r.next
		return x
	}
}

class UserParam {
	
	var name:String = null
	var dataType:Option[String] = None
	var unitAccession:Option[String] = None
	var unitCvRef:Option[String] = None
	var unitName:Option[String] = None
	var value:Option[String] = None
	
	
	def write(w:XmlWriter) = {
		import TraML._
		
		w.startElement(USER_PARAM)
		w.writeAttribute(NAME, name)
		w.writeOptional(TYPE, dataType)
		w.writeOptional(UNIT_ACCESSION, unitAccession)
		w.writeOptional(UNIT_CV_REF, unitCvRef)
		w.writeOptional(UNIT_NAME, unitName)
		w.writeOptional(VALUE, value)
		w.endElement
	}
}