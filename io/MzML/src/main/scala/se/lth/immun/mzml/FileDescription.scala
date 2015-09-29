package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object FileDescription {
	
	import MzML._
	
	def fromFile(r:XmlReader) = {
		var x = new FileDescription
		
		var e = r.top
		r.next
		while (r.in(e))
			r.top.name match {
				case FILE_CONTENT =>
					x.fileContent = FileContent.fromFile(r)
				case SOURCE_FILE_LIST => {
					r.next
					while (r.was(SOURCE_FILE))
						x.sourceFiles += SourceFile.fromFile(r)
				}
				case CONTACT => {
					x.contacts += Contact.fromFile(r)
				}
				case _ => {
					r.skipThis
					r.next
				}
			}
		
		x
	}
}

class FileDescription {
	var fileContent:FileContent = null
	var sourceFiles = new ArrayBuffer[SourceFile]
	var contacts	= new ArrayBuffer[Contact]
	
	def write(w:XmlWriter) = {
		import MzML._
		
		w.startElement(FILE_DESCRIPTION)
		
		fileContent.write(w)
		
		if (!sourceFiles.isEmpty) {
			w.startListElement(SOURCE_FILE_LIST, sourceFiles)
			for (s <- sourceFiles) s.write(w)
			w.endElement
		}
		
		for (c <- contacts) c.write(w)
		
		w.endElement
	}
}