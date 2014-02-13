package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object ScanSettings {
	
	import MzML._
	
	def fromFile(r:XmlReader) = {
		var x = new ScanSettings
		
		x.id = r.readAttribute(ID)
		
		var e = r.top
		r.next
		while (r.in(e))
			r.top.name match {
				case CV_PARAM => 
					x.cvParams += CvParam.fromFile(r)
				case USER_PARAM => 
					x.userParams += UserParam.fromFile(r)
				case REFERENCEABLE_PARAM_GROUP_REF => {
					x.paramGroupRefs += r.readAttribute(REF)
					r.next
				}
				case SOURCE_FILE_REF_LIST => {
					r.next
					while (r.was(SOURCE_FILE_REF)) {
						x.sourceFileRefs += r.readAttribute(REF)
						r.next
					}
				}
				case TARGET_LIST => {
					r.next
					while (r.was(TARGET))
						x.targets += Target.fromFile(r)
				}
				case _ => r.skipThis
			}
		
		x
	}
}

class ScanSettings {
	var id:String = null
	
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	var paramGroupRefs = new ArrayBuffer[String]
	var sourceFileRefs = new ArrayBuffer[String]
	var targets = new ArrayBuffer[Target]
	
	
	def write(w:XmlWriter) = {
		import MzML._
		
		w.startElement(SCAN_SETTINGS)
		w.writeAttribute(ID, id)
		for (x <- paramGroupRefs) ReferenceableParamGroupRef.write(w, x)
		for (x <- cvParams) x.write(w)
		for (x <- userParams) x.write(w)
	
		if (!sourceFileRefs.isEmpty) {
			w.startListElement(SOURCE_FILE_REF_LIST, sourceFileRefs)
			for (s <- sourceFileRefs) {
				w.startElement(SOURCE_FILE_REF)
				w.writeAttribute(REF, s)
				w.endElement
			}
			w.endElement
		}
	
		if (!targets.isEmpty) {
			w.startListElement(TARGET_LIST, targets)
			for (x <- targets) x.write(w)
			w.endElement
		}
		
		w.endElement
	}
}