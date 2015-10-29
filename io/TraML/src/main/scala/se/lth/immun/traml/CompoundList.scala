package se.lth.immun.traml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object CompoundList {
	
	import TraML._
	
	def fromFile(r:XmlReader, repFreq:Int):CompoundList = {
		var x = new CompoundList
		var e = r.top
		
		if (repFreq > 0)
			println("num peptides and compounds\tpep-comp-ID")
		
		r.next
		while (r.in(e))
			r.top.name match {
				case CV_PARAM => 
					x.cvParams += CvParam.fromFile(r)
				case USER_PARAM => 
					x.userParams += UserParam.fromFile(r)
				case PEPTIDE => 
					x.peptides += Peptide.fromFile(r)
					if (repFreq > 0 && (x.peptides.size + x.compounds.size) % repFreq == 0)
						println("%d\t%s".format(x.peptides.size + x.compounds.size, x.peptides.last.id))
		
				case COMPOUND => 
					x.compounds += Compound.fromFile(r)
					if (repFreq > 0 && (x.peptides.size + x.compounds.size) % repFreq == 0)
						println("%d\t%s".format(x.peptides.size + x.compounds.size, x.compounds.last.id))
						
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