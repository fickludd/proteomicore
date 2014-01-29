package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object Precursor {
	
	import MzML._
	
	def fromFile(r:XmlReader) = {
		var x = new Precursor
		
		x.externalSpectrumID = r.readOptional(EXTERNAL_SPECTRUM_ID)
		x.sourceFileRef = r.readOptional(SOURCE_FILE_REF)
		x.spectrumRef = r.readOptional(SPECTRUM_REF)
		
		var e = r.top
		r.next
		while (r.in(e))
			r.top.name match {
				case ISOLATION_WINDOW => 
					x.isolationWindow = Some(IsolationWindow.fromFile(r))
				case SELECTED_ION_LIST => {
					r.next
					while (r.was(SELECTED_ION))
						x.selectedIons += SelectedIon.fromFile(r)
				}
				case ACTIVATION => 
					x.activation = Activation.fromFile(r)
				case _ => r.skipThis
			}
		
		x
	}
}

class Precursor {
	var externalSpectrumID:Option[String] = None
	var sourceFileRef:Option[String] = None
	var spectrumRef:Option[String] = None
	
	var isolationWindow:Option[IsolationWindow] = None
	var selectedIons = new ArrayBuffer[SelectedIon]
	var activation:Activation = null
	
	
	def write(w:XmlWriter) = {
		import MzML._
		
		w.startElement(PRECURSOR)
		w.writeOptional(EXTERNAL_SPECTRUM_ID, externalSpectrumID)
		w.writeOptional(SOURCE_FILE_REF, sourceFileRef)
		w.writeOptional(SPECTRUM_REF, spectrumRef)
		
		if (isolationWindow.isDefined)
			isolationWindow.get.write(w)
		
		if (!selectedIons.isEmpty) {
			w.startListElement(SELECTED_ION_LIST, selectedIons)
			for (s <- selectedIons) s.write(w)
			w.endElement
		}
		
		activation.write(w)

		w.endElement
	}
}