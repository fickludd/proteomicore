package se.lth.immun.traml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object Modification {
	
	import TraML._
	
	def fromFile(r:XmlReader):Modification = {
		var x = new Modification
		var e = r.top
		
		x.location = r.readAttributeInt(LOCATION)
		x.averageMassDelta = r.readOptionalDouble(AVERAGE_MASS_DELTA)
		x.monoisotopicMassDelta = r.readOptionalDouble(MONOISOTOPIC_MASS_DELTA)

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

class Modification {
	var averageMassDelta:Option[Double] = None
	var location:Int = -1
	var monoisotopicMassDelta:Option[Double] = None
	
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	
	
	def write(w:XmlWriter) = {
		import TraML._
		
		w.startElement(MODIFICATION)
		w.writeAttribute(LOCATION, location)
		w.writeOptional(AVERAGE_MASS_DELTA, averageMassDelta)
		w.writeOptional(MONOISOTOPIC_MASS_DELTA, monoisotopicMassDelta)
		
		cvParams.foreach(x => x.write(w))
		userParams.foreach(x => x.write(w))
		w.endElement
	}
}