package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object InstrumentConfiguration {
	
	import MzML._
	
	def fromFile(r:XmlReader) = {
		var x = new InstrumentConfiguration
		
		x.id = r.readAttribute(ID)
		x.scanSettingsRef = r.readOptional(SCAN_SETTINGS_REF)
		
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
				case COMPONENT_LIST => 
					x.componentList = Some(ComponentList.fromFile(r))
				case SOFTWARE_REF => {
					x.softwareRef = Some(r.readAttribute(REF))
					r.next
				}
				case _ => {r.skipThis; r.next }
			}
		
		x
	}
}

class InstrumentConfiguration {
	var id:String = null
	var scanSettingsRef:Option[String] = None
	var softwareRef:Option[String] = None
	var componentList:Option[ComponentList] = None
	
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	var paramGroupRefs = new ArrayBuffer[String]
	
	
	def write(w:XmlWriter) = {
		import MzML._
		
		w.startElement(INSTRUMENT_CONFIGURATION)
		w.writeAttribute(ID, id)
		w.writeOptional(SCAN_SETTINGS_REF, scanSettingsRef)
		for (x <- paramGroupRefs) ReferenceableParamGroupRef.write(w, x)
		for (x <- cvParams) x.write(w)
		for (x <- userParams) x.write(w)
		if (componentList.isDefined)
			componentList.get.write(w)
		if (softwareRef.isDefined) {
			w.startElement(SOFTWARE_REF)
			w.writeAttribute(REF, softwareRef.get)
			w.endElement
		}
			
		w.endElement
	}
}