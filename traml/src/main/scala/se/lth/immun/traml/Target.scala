package se.lth.immun.traml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object Target {
	
	import TraML._
	
	def fromFile(r:XmlReader):Target = {
		var x = new Target
		var e = r.top
		
		x.id = r.readAttribute(ID)
		x.compoundRef = r.readOptional(COMPOUND_REF)
		x.peptideRef = r.readOptional(PEPTIDE_REF)

		r.next
		while (r.in(e))
			r.top.name match {
				case CV_PARAM => 
					x.cvParams += CvParam.fromFile(r)
				case USER_PARAM => 
					x.userParams += UserParam.fromFile(r)
				case PRECURSOR => 
					x.precursor = Precursor.fromFile(r)
				case RETENTION_TIME => 
					x.retentionTime = Some(RetentionTime.fromFile(r))
				case CONFIGURATION_LIST => {
					r.next
					while (r.was(CONFIGURATION))
						x.configurations += Configuration.fromFile(r)
				}
				case _ => r.skipThis
			}
		
		return x
	}
}

class Target {
	var compoundRef:Option[String] = None
	var id:String = null
	var peptideRef:Option[String] = None
	
	var precursor:Precursor = null
	var retentionTime:Option[RetentionTime] = None
	var configurations = new ArrayBuffer[Configuration]
	
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	
	
	def write(w:XmlWriter) = {
		import TraML._
		
		w.startElement(TARGET)
		w.writeAttribute(ID, id)
		w.writeOptional(COMPOUND_REF, compoundRef)
		w.writeOptional(PEPTIDE_REF, peptideRef)
		
		cvParams.foreach(x => x.write(w))
		userParams.foreach(x => x.write(w))
		
		precursor.write(w)
		if (retentionTime.isDefined)
			retentionTime.get.write(w)
		
		if (!configurations.isEmpty) {
			w.startElement(CONFIGURATION_LIST)
			configurations.foreach(_.write(w))
			w.endElement
		}
		
		w.endElement
	}
}