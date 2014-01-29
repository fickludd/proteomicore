package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object ReferenceableParamGroupRef {
	
	import MzML._
	
	def fromFile(r:XmlReader) = {
		var x = new ReferenceableParamGroupRef
		
		x.ref = r.readAttribute(REF)
		r.next
		
		x
	}
	
	def write(w:XmlWriter, ref:String) = {
		w.startElement(REFERENCEABLE_PARAM_GROUP_REF)
		w.writeAttribute(REF, ref)
		w.endElement
	}
}

class ReferenceableParamGroupRef {
	var ref:String = null
}