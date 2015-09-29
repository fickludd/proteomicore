package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer
import java.nio.channels.FileChannel

object Spectrum {
	
	import MzML._
	
	def fromFile(r:XmlReader, binaryByteChannel:FileChannel) = {
		var x = new Spectrum
		
		x.id = r.readAttribute(ID)
		x.defaultArrayLength = r.readAttributeInt(DEFAULT_ARRAY_LENGTH)
		x.index = r.readAttributeInt(INDEX)
		x.dataProcessingRef = r.readOptional(DATA_PROCESSING_REF)
		x.sourceFileRef = r.readOptional(SOURCE_FILE_REF)
		x.spotID = r.readOptional(SPOT_ID)
		
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
				case SCAN_LIST => 
					x.scanList = Some(ScanList.fromFile(r))
				case PRECURSOR_LIST => {
					r.next
					while (r.was(PRECURSOR))
						x.precursors += Precursor.fromFile(r)
				}
				case PRODUCT_LIST => {
					r.next
					while (r.was(PRODUCT))
						x.products += Product.fromFile(r)
				}
				case BINARY_DATA_ARRAY_LIST => {
					r.next
					while (r.was(BINARY_DATA_ARRAY))
						x.binaryDataArrays += BinaryDataArray.fromFile(r, binaryByteChannel)
				}
				case _ => r.skipThis
			}
		
		x
	}
}

class Spectrum {
	import MzML._

	var id:String = null
	var defaultArrayLength:Int = -1
	var index:Int = -1
	var dataProcessingRef:Option[String] = None
	var sourceFileRef:Option[String] = None
	var spotID:Option[String] = None
	
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	var paramGroupRefs = new ArrayBuffer[String]
	
	var scanList:Option[ScanList] = None
	var precursors = new ArrayBuffer[Precursor]
	var products = new ArrayBuffer[Product]
	var binaryDataArrays = new ArrayBuffer[BinaryDataArray]

	
	
	def write(
			w:XmlWriter, 
			binaryByteChannel:FileChannel,
			scanTime:Option[Double] = None,
			spotId:Option[String] = None
	):OffsetRef = {
		
		val byteOffset = w.byteOffset
		
		w.startElement(SPECTRUM)
		w.writeAttribute(INDEX, index)
		w.writeAttribute(ID, id)
		w.writeOptional(DATA_PROCESSING_REF, dataProcessingRef)
		w.writeOptional(SPOT_ID, spotID)
		w.writeAttribute(DEFAULT_ARRAY_LENGTH, defaultArrayLength)
		w.writeOptional(SOURCE_FILE_REF, sourceFileRef)
		
		for (x <- paramGroupRefs) ReferenceableParamGroupRef.write(w, x)
		for (x <- cvParams) x.write(w)
		for (x <- userParams) x.write(w)
		
		if (scanList.isDefined)
			scanList.get.write(w)
		
		if (!precursors.isEmpty) {
			w.startListElement(PRECURSOR_LIST, precursors)
			for (x <- precursors) x.write(w)
			w.endElement
		}
		
		if (!products.isEmpty) {
			w.startListElement(PRODUCT_LIST, products)
			for (x <- products) x.write(w)
			w.endElement
		}
		
		if (!binaryDataArrays.isEmpty) {
			w.startListElement(BINARY_DATA_ARRAY_LIST, binaryDataArrays)
			for (x <- binaryDataArrays) x.write(w, binaryByteChannel)
			w.endElement
		}
			
		w.endElement
		
		OffsetRef(id, byteOffset, scanTime, spotId)
	}
}