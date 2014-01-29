package se.lth.immun.traml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object CompoundList {
	
	import TraML._
	
	def fromFile(r:XmlReader):CompoundList = {
		var x = new CompoundList
		var e = r.top
		
		r.next
		while (r.in(e))
			r.top.name match {
				case CV_PARAM => 
					x.cvParams += CvParam.fromFile(r)
				case USER_PARAM => 
					x.userParams += UserParam.fromFile(r)
				case PEPTIDE => 
					x.peptides += Peptide.fromFile(r)
				case COMPOUND => 
					x.compounds += Compound.fromFile(r)
				case _ => r.skipThis
			}
		
		return x
	}
}

class CompoundList {

	var compounds = new ArrayBuffer[Compound]
	var peptides = new ArrayBuffer[Peptide]
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	
	
	def write(w:XmlWriter) = {
		import TraML._
		
		w.startElement(COMPOUND_LIST)
		
		cvParams.foreach(x => x.write(w))
		userParams.foreach(x => x.write(w))
		peptides.foreach(x => x.write(w))
		compounds.foreach(x => x.write(w))
		
		w.endElement
	}
}