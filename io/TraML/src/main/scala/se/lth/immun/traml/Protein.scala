package se.lth.immun.traml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object Protein {
	
	import TraML._
	
	def fromFile(r:XmlReader):Protein = {
		var x = new Protein
		var e = r.top
		
		x.id = r.readAttribute(ID)

		r.next
		while (r.in(e))
			r.top.name match {
				case CV_PARAM => 
					x.cvParams += CvParam.fromFile(r)
				case USER_PARAM => 
					x.userParams += UserParam.fromFile(r)
				case PROTEIN_SEQUENCE => {
					x.sequence = r.text.trim
					r.next
				}
				case _ => r.skipThis
			}
		
		return x
	}
}

class Protein {
	var id:String = null
	var sequence:String = null
	
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	
	
	def write(w:XmlWriter) = {
		import TraML._
		
		w.startElement(PROTEIN)
		w.writeAttribute(ID, id)
		
		cvParams.foreach(x => x.write(w))
		userParams.foreach(x => x.write(w))
		if (sequence != null) {
			w.startElement(PROTEIN_SEQUENCE)
			w.text(sequence)
			w.endElement
		}
		w.endElement
	}
}