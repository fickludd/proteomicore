package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object ScanList {
	
	import MzML._
	
	def fromFile(r:XmlReader) = {
		var x = new ScanList
		
		var e = r.top
		r.next
		while (r.in(e))
			r.top.name match {
				case CV_PARAM => 
					x.cvParams += CvParam.fromFile(r)
				case USER_PARAM => 
					x.userParams += UserParam.fromFile(r)
				case SCAN => 
					x.scans += Scan.fromFile(r)
				case REFERENCEABLE_PARAM_GROUP_REF => {
					x.paramGroupRefs += r.readAttribute(REF)
					r.next
				}
				case _ => r.skipThis
			}
		
		x
	}
}

class ScanList {
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	var paramGroupRefs = new ArrayBuffer[String]
	var scans = new ArrayBuffer[Scan]
	
	
	def write(w:XmlWriter) = {
		import MzML._
		
		w.startListElement(SCAN_LIST, scans)
		for (x <- paramGroupRefs) ReferenceableParamGroupRef.write(w, x)
		for (x <- cvParams) x.write(w)
		for (x <- userParams) x.write(w)
		for (x <- scans) x.write(w)
		w.endElement
	}
}