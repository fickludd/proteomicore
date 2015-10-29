package se.lth.immun.traml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object TraML {
	
	val TRAML 					= "TraML"
	val XMLNS 					= "xmlns"
	val XMLNS_XSI 				= "xmlns:xsi"
	val XMLNS_SCHEMA_LOCATION 	= "xmlns:schemaLocation"
	val VERSION 				= "version"
	
	val CV_LIST 			= "cvList"
	val SOURCE_FILE_LIST 	= "SourceFileList"
	val CONTACT_LIST 		= "ContactList"
	val PUBLICATION_LIST 	= "PublicationList"
	val INSTRUMENT_LIST 	= "InstrumentList"
	val SOFTWARE_LIST 		= "SoftwareList"
	val PROTEIN_LIST 		= "ProteinList"
	val COMPOUND_LIST 		= "CompoundList"
	val TRANSITION_LIST 	= "TransitionList"
	val TARGET_LIST 		= "TargetList"
	
	val CV 			= "cv"
	val URI 		= "URI"
	val FULL_NAME 	= "fullName"
	
	val ID 			= "id"
	val REF 		= "ref"
	val CV_REF 		= "cvRef"
	val NAME 		= "name"
	val LOCATION 	= "location"
	val VALUE 		= "value"
	val COUNT 		= "count"
	val UNIT_ACCESSION 	= "unitAccession"
	val UNIT_CV_REF 	= "unitCvRef"
	val UNIT_NAME 		= "unitName"
	val ACCESSION 		= "accession"
	val TYPE 			= "type"
	val USER_PARAM 		= "userParam"
	val SOURCE_FILE 	= "SourceFile"
	
	val CONTACT 		= "Contact"
	val PUBLICATION 	= "Publication"
	val INSTRUMENT 		= "Instrument"
	val SOFTWARE 		= "Software"
		
	val PROTEIN 				= "Protein"
	val PROTEIN_ACCESSION_ACC 	= "MS:1000885"
	val PROTEIN_SEQUENCE 		= "Sequence"
		
	val COMPOUND 	= "Compound"
	val PEPTIDE 	= "Peptide"
	val SEQUENCE 	= "sequence"
	val PROTEIN_REF 			= "ProteinRef"
	val MODIFICATION 			= "Modification"
	val RETENTION_TIME_LIST 	= "RetentionTimeList"
	val RETENTION_TIME 			= "RetentionTime"
	val CV_PARAM 		= "cvParam"
	val RT_START_ACC 	= "MS:1000916"
	val RT_END_ACC 		= "MS:1000917"
	val EVIDENCE 		= "Evidence"
		
	val MONOISOTOPIC_MASS_DELTA 	= "monoisotopicMassDelta"
	val AVERAGE_MASS_DELTA	 		= "averageMassDelta"
		
	val TRANSITION 		= "Transition"
	val PEPTIDE_REF 	= "peptideRef"
	val PRECURSOR 		= "Precursor"
	val Q1_ACC 			= "MS:1000827"
	val PRODUCT 		= "Product"
	val Q3_ACC 			= "MS:1000827"
	val INTERPRETATION_LIST 	= "InterpretationList"
	val INTERPRETATION 			= "Interpretation"
	val Y_ION_ACC 				= "MS:1001220"
	val B_ION_ACC 				= "MS:1001224"
	val ION_ORDINAL_ACC 		= "MS:1000903"
	val CONFIGURATION_LIST 		= "ConfigurationList"
	val CONFIGURATION 			= "Configuration"
	val CE_ACC 					= "MS:1000045"
		
	val COMPOUND_REF 			= "compoundRef"
	val INTERMEDIATE_PRODUCT 	= "IntermediateProduct"
	val PREDICTION 				= "Prediction"
	val INSTRUMENT_REF 			= "instrumentRef"
	val CONFIGURATION_REF 		= "configurationRef"
	val VALIDATION_STATUS 		= "ValidationStatus"
	val CONTACT_REF 			= "contactRef"
	val SOFTWARE_REF 			= "softwareRef"
	val TARGET_INCLUDE_LIST 	= "TargetIncludeList"
	val TARGET_EXCLUDE_LIST 	= "TargetExcludeList"
	val TARGET 					= "Target"
		
	def fromFile(r:XmlReader, repFreq:Int = 0) = {
		var t = new TraML
		
		r.until(TRAML)
		t.id 					= r.readOptional(ID)
		t.xmlns 				= r.readOptional(XMLNS)
		t.xmlns_xsi 			= r.readOptional(XMLNS_XSI)
		t.xmlns_schemaLocation 	= r.readOptional(XMLNS_SCHEMA_LOCATION)
		t.version 				= r.readAttribute(VERSION)
		
		var e = r.top
		r.next
		while (r.in(e))
			r.top.name match {
				case CV_LIST => {
					r.next
					while (r.was(CV))
						t.cvs += Cv.fromFile(r)
				}
				case SOURCE_FILE_LIST => {
					r.next
					while (r.was(SOURCE_FILE))
						t.sourceFiles += SourceFile.fromFile(r)
				}
				case CONTACT_LIST => {
					r.next
					while (r.was(CONTACT))
						t.contacts += Contact.fromFile(r)
				}
				case PUBLICATION_LIST => {
					r.next
					while (r.was(PUBLICATION))
						t.publications += Publication.fromFile(r)
				}
				case INSTRUMENT_LIST => {
					r.next
					while (r.was(INSTRUMENT))
						t.instruments += Instrument.fromFile(r)
				}
				case SOFTWARE_LIST => {
					r.next
					while (r.was(SOFTWARE))
						t.softwares += Software.fromFile(r)
				}
				case PROTEIN_LIST => {
					r.next
					if (repFreq > 0)
						println("num proteins\tprotName")
					while (r.was(PROTEIN)) {
						t.proteins += Protein.fromFile(r)
						if (repFreq > 0 && t.proteins.size % repFreq == 0)
							println("%d\t%s".format(t.proteins.size, t.proteins.last.id))
					}
				}
				case COMPOUND_LIST => {
					t.compoundList = CompoundList.fromFile(r, repFreq)
				}
				case TRANSITION_LIST => {
					r.next
					if (repFreq > 0)
						println("num transitions\ttransID")
					while (r.was(TRANSITION)) {
						t.transitions += Transition.fromFile(r)
						if (repFreq > 0 && t.transitions.size % repFreq == 0)
							println("%d\t%s".format(t.transitions.size, t.transitions.last.id))
					}
				}
				case TARGET_LIST => {
					t.targetList = Some(TargetList.fromFile(r, repFreq))
				}
				case _ => {
					r.skipThis
					r.next
				}
			}
		t
	}
	
	
	
	def main(args:Array[String]) = {
		import java.io.FileReader
		import java.io.File
		
		var f = fromFile(new XmlReader(new FileReader(new File(args(0)))))
	}
}




class TraML {

	var id:Option[String] 						= None
	var version:String 							= null
	var xmlns:Option[String] 					= None
	var xmlns_xsi:Option[String] 				= None
	var xmlns_schemaLocation:Option[String] 	= None
	
	var cvs 			= new ArrayBuffer[Cv]
	var sourceFiles 	= new ArrayBuffer[SourceFile]
	var contacts 		= new ArrayBuffer[Contact]
	var publications 	= new ArrayBuffer[Publication]
	var instruments 	= new ArrayBuffer[Instrument]
	var softwares 		= new ArrayBuffer[Software]
	var proteins 		= new ArrayBuffer[Protein]
	var compoundList:CompoundList 		= null
	var transitions 					= new ArrayBuffer[Transition]
	var targetList:Option[TargetList] 	= None
	
	
	
	def write(w:XmlWriter) = {
		import TraML._
		
		w.startDocument
		w.startElement(TRAML)
		w.writeOptional(ID, id)
		w.writeOptional(XMLNS, xmlns)
		w.writeOptional(XMLNS_XSI, xmlns_xsi)
		w.writeOptional(XMLNS_SCHEMA_LOCATION, xmlns_schemaLocation)
		w.writeAttribute(VERSION, version)
		
		w.startElement(CV_LIST)
		for (cv <- cvs) cv.write(w)
		w.endElement
		
		w.startElement(SOURCE_FILE_LIST)
		for (sf <- sourceFiles) sf.write(w)
		w.endElement
		
		w.startElement(CONTACT_LIST)
		for (c <- contacts) c.write(w)
		w.endElement
		
		w.startElement(PUBLICATION_LIST)
		for (p <- publications) p.write(w)
		w.endElement
		
		w.startElement(INSTRUMENT_LIST)
		for (i <- instruments) i.write(w)
		w.endElement
		
		w.startElement(SOFTWARE_LIST)
		for (s <- softwares) s.write(w)
		w.endElement
		
		w.startElement(PROTEIN_LIST)
		for (p <- proteins) p.write(w)
		w.endElement
		
		compoundList.write(w)
		
		w.startElement(TRANSITION_LIST)
		for (t <- transitions) t.write(w)
		w.endElement
		
		if (targetList.isDefined)
			targetList.get.write(w)
		
		w.endElement
		w.endDocument
	}
}