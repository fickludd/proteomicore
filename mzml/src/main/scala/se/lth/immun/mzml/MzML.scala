package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer
import java.nio.channels.FileChannel

object MzML {

	val INDEXED_MZML 	= "indexedmzML"
	val MZML 			= "mzML"
	val XMLNS 					= "xmlns"
	val XMLNS_XSI 				= "xmlns:xsi"
	val XSI_SCHEMA_LOCATION 	= "xsi:schemaLocation"
	val VERSION 				= "version"
	
	val CV_LIST 			= "cvList"
	val FILE_DESCRIPTION	= "fileDescription"
	val FILE_CONTENT		= "fileContent"
		
	val CV 			= "cv"
	val URI 		= "URI"
	val FULL_NAME 	= "fullName"
	val CV_PARAM 		= "cvParam"
	val USER_PARAM 		= "userParam"
	val REFERENCEABLE_PARAM_GROUP_REF = "referenceableParamGroupRef"
	val REFERENCEABLE_PARAM_GROUP = "referenceableParamGroup"
	val REFERENCEABLE_PARAM_GROUP_LIST = "referenceableParamGroupList"
	val SAMPLE = "sample"
	val SAMPLE_LIST = "sampleList"
	val SAMPLE_REF = "sampleRef"
	
	val CONTACT 		= "contact"
		
	val SOFTWARE_LIST 		= "softwareList"
	val SOFTWARE 			= "software"
	val SOFTWARE_REF		= "softwareRef"
		
	val SCAN_SETTINGS_LIST 		= "scanSettingsList"
	val SCAN_SETTINGS 			= "scanSettings"
	val SCAN_SETTINGS_REF 		= "scanSettingsRef"
		
	val SOURCE_FILE 	= "sourceFile"
	val SOURCE_FILE_LIST 	= "sourceFileList"
	val SOURCE_FILE_REF_LIST 	= "sourceFileRefList"
	val SOURCE_FILE_REF 		= "sourceFileRef"
	val DEFAULT_SOURCE_FILE_REF = "defaultSourceFileRef"
		
	val TARGET_LIST 	= "targetList"
	val TARGET 			= "target"
		
	val INSTRUMENT_CONFIGURATION_LIST 	= "instrumentConfigurationList"
	val INSTRUMENT_CONFIGURATION 		= "instrumentConfiguration"
	val DEFAULT_INSTRUMENT_CONFIGURATION_REF = "defaultInstrumentConfigurationRef"
	val INSTRUMENT_CONFIGURATION_REF = "instrumentConfigurationRef"
		
	val DATA_PROCESSING_LIST 	= "dataProcessingList"
	val DATA_PROCESSING 		= "dataProcessing"
	val PROCESSING_METHOD		= "processingMethod"
	val DEFAULT_DATA_PROCESSING_REF = "defaultDataProcessingRef"
	val DATA_PROCESSING_REF 	= "dataProcessingRef"

	val COMPONENT_LIST = "componentList"
	val ORDER = "order"
	val SOURCE = "source"
	val DETECTOR = "detector"
	val ANALYZER = "analyzer"
	
	val SPECTRUM_LIST = "spectrumList"
	val SPECTRUM = "spectrum"
	val START_TIMESTAMP = "startTimeStamp"
	val DEFAULT_ARRAY_LENGTH = "defaultArrayLength"
	val INDEX = "index"
	val SPOT_ID = "spotID"
	val EXTERNAL_SPECTRUM_ID = "externalSpectrumID"
	val SPECTRUM_REF = "spectrumRef"
		
	val SCAN_LIST = "scanList"
	val SCAN = "scan"
		
	val SCAN_WINDOW_LIST = "scanWindowList"
	val SCAN_WINDOW = "scanWindow"
		
	val PRECURSOR_LIST = "precursorList"
	val PRECURSOR = "precursor"
		
	val PRODUCT_LIST = "productList"
	val PRODUCT = "product"
	val ISOLATION_WINDOW = "isolationWindow"
	val ACTIVATION = "activation"
	
	val SELECTED_ION_LIST = "selectedIonList"
	val SELECTED_ION = "selectedIon"
	
	val BINARY_DATA_ARRAY_LIST = "binaryDataArrayList"
	val BINARY_DATA_ARRAY = "binaryDataArray"
	val BINARY = "binary"
	val ARRAY_LENGTH = "arrayLength"
	val ENCODED_LENGTH = "encodedLength"
	
	val CHROMATOGRAM_LIST = "chromatogramList"
	val CHROMATOGRAM = "chromatogram"
		
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
	val RUN		= "run"
	
	def fromFile(r:XmlReader, dataHandlers:MzMLDataHandlers, binaryByteChannel:FileChannel = null) = {
		var t = new MzML
		
		while (!r.is(MZML)) {
			if (r.is(INDEXED_MZML)) {
				t.idx_xmlns 				= r.readOptional(XMLNS)
				t.idx_xmlns_xsi 			= r.readOptional(XMLNS_XSI)
				t.idx_xsi_schemaLocation 	= r.readOptional(XSI_SCHEMA_LOCATION)
			} else r.next
		}
		t.version = r.readAttribute(VERSION)
		t.id = r.readOptional(ID)
		t.xmlns 				= r.readOptional(XMLNS)
		t.xmlns_xsi 			= r.readOptional(XMLNS_XSI)
		t.xsi_schemaLocation 	= r.readOptional(XSI_SCHEMA_LOCATION)
		t.accession = r.readOptional(ACCESSION)
		
		var e = r.top
		r.next
		while (r.in(e))
			r.top.name match {
				case CV_LIST => {
					r.next
					while (r.was(CV))
						t.cvs += Cv.fromFile(r)
				}
				case FILE_DESCRIPTION => {
					t.fileDescription = FileDescription.fromFile(r)
				}
				case REFERENCEABLE_PARAM_GROUP_LIST => {
					r.next
					while (r.was(REFERENCEABLE_PARAM_GROUP))
						t.referenceableParamGroups += ReferenceableParamGroup.fromFile(r)
				}
				case SAMPLE_LIST => {
					r.next
					while (r.was(SAMPLE))
						t.samples += Sample.fromFile(r)
				}
				case SOFTWARE_LIST => {
					r.next
					while (r.was(SOFTWARE))
						t.softwares += Software.fromFile(r)
				}
				case SCAN_SETTINGS_LIST => {
					r.next
					while (r.was(SCAN_SETTINGS))
						t.scanSettings += ScanSettings.fromFile(r)
				}
				case INSTRUMENT_CONFIGURATION_LIST => {
					r.next
					while (r.was(INSTRUMENT_CONFIGURATION))
						t.instrumentConfigurations += InstrumentConfiguration.fromFile(r)
				}
				case DATA_PROCESSING_LIST => {
					r.next
					while (r.was(DATA_PROCESSING))
						t.dataProcessings += DataProcessing.fromFile(r)
				}
				case RUN => 
					t.run = Run.fromFile(r, dataHandlers, binaryByteChannel)
				case _ => {r.skipThis; r.next}
			}
		
		t
	}
	
	
	
	def main(args:Array[String]) = {
		import java.io.FileReader
		import java.io.File
		
		var dh = new MzMLDataHandlers(i => {}, s => {}, i => {}, c => {})
		var f = fromFile(new XmlReader(new FileReader(new File(args(0)))), dh)
	}
}

class MzML {
	var version:String = null
	var id:Option[String] = None
	var accession:Option[String] = None
	var xmlns:Option[String] 				= None
	var xmlns_xsi:Option[String] 			= None
	var xsi_schemaLocation:Option[String] 	= None
	var idx_xmlns:Option[String] 				= None
	var idx_xmlns_xsi:Option[String] 			= None
	var idx_xsi_schemaLocation:Option[String] 	= None
	
	var cvs 			= new ArrayBuffer[Cv]
	var fileDescription:FileDescription = null
	var referenceableParamGroups = new ArrayBuffer[ReferenceableParamGroup]
	var samples 			= new ArrayBuffer[Sample]
	var softwares 			= new ArrayBuffer[Software]
	var scanSettings 		= new ArrayBuffer[ScanSettings]
	var instrumentConfigurations 	= new ArrayBuffer[InstrumentConfiguration]
	var dataProcessings 	= new ArrayBuffer[DataProcessing]
	var run:Run = null
	
	
	
	def write(w:XmlWriter, dw:MzMLDataWriters) = {
		import MzML._
		
		w.startDocument()
		w.startElement(INDEXED_MZML)
		w.writeOptional(XMLNS, idx_xmlns)
		w.writeOptional(XMLNS_XSI, idx_xmlns_xsi)
		w.writeOptional(XSI_SCHEMA_LOCATION, idx_xsi_schemaLocation)
		
		w.startElement(MZML)
		w.writeOptional(XMLNS, xmlns)
		w.writeOptional(XMLNS_XSI, xmlns_xsi)
		w.writeOptional(XSI_SCHEMA_LOCATION, xsi_schemaLocation)
		w.writeOptional(ID, id)
		w.writeAttribute(VERSION, version)
		
		w.startListElement(CV_LIST, cvs)
		for (cv <- cvs) cv.write(w)
		w.endElement
		
		fileDescription.write(w)
		
		if (!referenceableParamGroups.isEmpty) {
			w.startListElement(REFERENCEABLE_PARAM_GROUP_LIST, referenceableParamGroups)
			for (rf <- referenceableParamGroups) rf.write(w)
			w.endElement
		}
	
		if (!samples.isEmpty) {
			w.startListElement(SAMPLE_LIST, samples)
			for (s <- samples) s.write(w)
			w.endElement
		}
		
		w.startListElement(SOFTWARE_LIST, softwares)
		for (s <- softwares) s.write(w)
		w.endElement
		
		if (!scanSettings.isEmpty) {
			w.startListElement(SCAN_SETTINGS_LIST, scanSettings)
			for (s <- scanSettings) s.write(w)
			w.endElement
		}
		
		w.startListElement(INSTRUMENT_CONFIGURATION_LIST, instrumentConfigurations)
		for (x <- instrumentConfigurations) x.write(w)
		w.endElement
		
		w.startListElement(DATA_PROCESSING_LIST, dataProcessings)
		for (x <- dataProcessings) x.write(w)
		w.endElement
		
		run.write(w, dw)
		
		w.endElement
		w.endElement
		w.endDocument
	}
}