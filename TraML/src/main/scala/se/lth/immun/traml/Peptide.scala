package se.lth.immun.traml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object Peptide {
	
	import TraML._
	
	def fromFile(r:XmlReader):Peptide = {
		var x = new Peptide
		var e = r.top
		
		x.id = r.readAttribute(ID)
		x.sequence = r.readAttribute(SEQUENCE)

		r.next
		while (r.in(e))
			r.top.name match {
				case CV_PARAM => 
					x.cvParams += CvParam.fromFile(r)
				case USER_PARAM => 
					x.userParams += UserParam.fromFile(r)
				case PROTEIN_REF => {
					x.proteinRefs += r.readAttribute(REF)
					r.next
				}
				case MODIFICATION => 
					x.modifications += Modification.fromFile(r)
				case RETENTION_TIME_LIST => {
					r.next
					while (r.was(RETENTION_TIME))
						x.retentionTimes += RetentionTime.fromFile(r)
				}
				case EVIDENCE => 
					x.evidence = Some(Evidence.fromFile(r))
				case _ => r.skipThis
			}
		
		return x
	}
}

class Peptide {
	var id:String = null
	var sequence:String = null
	
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	var proteinRefs = new ArrayBuffer[String]
	var modifications = new ArrayBuffer[Modification]
	var retentionTimes = new ArrayBuffer[RetentionTime]
	var evidence:Option[Evidence] = None
	
	
	def write(w:XmlWriter) = {
		import TraML._
		
		w.startElement(PEPTIDE)
		w.writeAttribute(ID, id)
		w.writeAttribute(SEQUENCE, sequence)
		
		cvParams.foreach(x => x.write(w))
		userParams.foreach(x => x.write(w))
		proteinRefs.foreach(x => {
			w.startElement(PROTEIN_REF)
			w.writeAttribute(REF, x)
			w.endElement
		})
		modifications.foreach(x => x.write(w))
		
		if (!retentionTimes.isEmpty) {
			w.startElement(RETENTION_TIME_LIST)
			retentionTimes.foreach(_.write(w))
			w.endElement
		}
		
		if (evidence.isDefined)
			evidence.get.write(w)
		
		w.endElement
	}
}