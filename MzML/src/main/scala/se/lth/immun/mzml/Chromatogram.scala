package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer
import java.nio.channels.FileChannel

object Chromatogram {
	
	import MzML._
	
	def fromFile(r:XmlReader, binaryByteChannel:FileChannel) = {
		var x = new Chromatogram
		
		x.id = r.readAttribute(ID)
		x.defaultArrayLength = r.readAttributeInt(DEFAULT_ARRAY_LENGTH)
		x.index = r.readAttributeInt(INDEX)
		x.dataProcessingRef = r.readOptional(DATA_PROCESSING_REF)
		
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
				case PRECURSOR => {
					x.precursor = Some(Precursor.fromFile(r))
				}
				case PRODUCT => {
					x.product = Some(Product.fromFile(r))
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

class Chromatogram {
	import MzML._
	
	var id:String = null
	var defaultArrayLength:Int = -1
	var index:Int = -1
	var dataProcessingRef:Option[String] = None
	
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	var paramGroupRefs = new ArrayBuffer[String]
	
	var precursor:Option[Precursor] = None
	var product:Option[Product] = None
	var binaryDataArrays = new ArrayBuffer[BinaryDataArray]

	
	
	def write(w:XmlWriter, binaryByteChannel:FileChannel):OffsetRef = {
		
		val byteOffset = w.byteOffset
		
		w.startElement(CHROMATOGRAM)
		w.writeAttribute(INDEX, index)
		w.writeAttribute(ID, id)
		w.writeAttribute(DEFAULT_ARRAY_LENGTH, defaultArrayLength)
		w.writeOptional(DATA_PROCESSING_REF, dataProcessingRef)
		
		for (x <- paramGroupRefs) ReferenceableParamGroupRef.write(w, x)
		for (x <- cvParams) x.write(w)
		for (x <- userParams) x.write(w)
		
		if (precursor.isDefined)
			precursor.get.write(w)
		
		if (product.isDefined)
			product.get.write(w)
			
		w.startListElement(BINARY_DATA_ARRAY_LIST, binaryDataArrays)
		for (x <- binaryDataArrays) x.write(w, binaryByteChannel)
		w.endElement
		
		w.endElement
		
		OffsetRef(id, byteOffset, None, None)
	}
}