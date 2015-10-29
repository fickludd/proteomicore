package se.lth.immun.traml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object CvParam {
	
	import TraML._
	
	def fromFile(r:XmlReader):CvParam = {
		var x = new CvParam
		x.accession = r.readAttribute(ACCESSION)
		x.cvRef = r.readAttribute(CV_REF)
		x.name = r.readAttribute(NAME)
		x.unitAccession = r.readOptional(UNIT_ACCESSION)
		x.unitCvRef = r.readOptional(UNIT_CV_REF)
		x.unitName = r.readOptional(UNIT_NAME)
		x.value = r.readOptional(VALUE)
		
		r.next
		return x
	}
	
	def MS[T](acc:String, name:String, value:Option[T], unit:Option[(String, String, String)]) = {
		val cv = new CvParam
		cv.cvRef = "MS"
		cv.accession = acc
		cv.name = name
		cv.value = value.map(_.toString)
		for ((ucv, uacc, uname) <- unit) {
			cv.unitCvRef = Some(ucv)
			cv.unitAccession = Some(uacc)
			cv.unitName = Some(uname)
		}
		cv
	}
}

class CvParam {
	var accession = ""
	var cvRef = ""
	var name = ""
	var unitAccession:Option[String] = None
	var unitCvRef:Option[String] = None
	var unitName:Option[String] = None
	var value:Option[String] = None
	
	def write(w:XmlWriter) = {
		import TraML._
		
		w.startElement(CV_PARAM)
		w.writeAttribute(CV_REF, cvRef)
		w.writeAttribute(ACCESSION, accession)
		w.writeAttribute(NAME, name)
		w.writeOptional(VALUE, value)
		w.writeOptional(UNIT_CV_REF, unitCvRef)
		w.writeOptional(UNIT_ACCESSION, unitAccession)
		w.writeOptional(UNIT_NAME, unitName)
		w.endElement
	}
}