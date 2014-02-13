package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object Product {
	
	import MzML._
	
	def fromFile(r:XmlReader) = {
		var x = new Product
		
		var e = r.top
		r.next
		while (r.in(e))
			r.top.name match {
				case ISOLATION_WINDOW => 
					x.isolationWindow = Some(IsolationWindow.fromFile(r))
				case _ => r.skipThis
			}
		
		x
	}
}

class Product {
	var isolationWindow:Option[IsolationWindow] = None
	
	
	def write(w:XmlWriter) = {
		import MzML._
		
		w.startElement(PRODUCT)
		if (isolationWindow.isDefined)
			isolationWindow.get.write(w)
		
		w.endElement
	}
}