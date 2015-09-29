package se.lth.immun.traml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object TargetList {
	
	import TraML._
	
	def fromFile(r:XmlReader):TargetList = {
		var x = new TargetList
		var e = r.top
		
		r.next
		while (r.in(e))
			r.top.name match {
				case CV_PARAM => 
					x.cvParams += CvParam.fromFile(r)
				case USER_PARAM => 
					x.userParams += UserParam.fromFile(r)
				case TARGET_INCLUDE_LIST => 
					r.next
					while (r.was(TARGET))
						x.targetIncludes += Target.fromFile(r)
				case TARGET_EXCLUDE_LIST => {
					r.next
					while (r.was(TARGET))
						x.targetExcludes += Target.fromFile(r)
				}
				case _ => r.skipThis
			}
		
		return x
	}
}

class TargetList {
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	
	var targetIncludes = new ArrayBuffer[Target]
	var targetExcludes = new ArrayBuffer[Target]
	
	
	def write(w:XmlWriter) = {
		import TraML._
		
		w.startElement(TARGET_LIST)
		cvParams.foreach(x => x.write(w))
		userParams.foreach(x => x.write(w))
		
		w.startElement(TARGET_INCLUDE_LIST)
		targetIncludes.foreach(_.write(w))
		w.endElement
		
		w.startElement(TARGET_EXCLUDE_LIST)
		targetExcludes.foreach(_.write(w))
		w.endElement
		
		w.endElement
	}
}