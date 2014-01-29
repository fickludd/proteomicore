package se.lth.immun.mzml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer
import java.nio.channels.FileChannel
import java.nio.ByteBuffer

object BinaryDataArray {
	
	import MzML._
	import IMzML._
	
	def fromFile(r:XmlReader, binaryByteChannel:FileChannel) = {
		var x = new BinaryDataArray
		
		x.arrayLength = r.readOptionalInt(ARRAY_LENGTH)
		x.dataProcessingRef = r.readOptional(DATA_PROCESSING_REF)
		x.encodedLength = r.readAttributeInt(ENCODED_LENGTH)
		
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
				case BINARY => {
					x.binary = r.text.trim
					r.next
				}
				case _ => r.skipThis
			}
		
		x.cvParams.find(_.accession == IMZML_EXTERNAL_DATA_ACC) match {
			case Some(cv) => {
				val extLength = x.cvParams.find(_.accession == IMZML_EXTERNAL_ARRAY_LENGTH_ACC)
				val extOffset = x.cvParams.find(_.accession == IMZML_EXTERNAL_OFFSET_ACC)
				val extEncLength = x.cvParams.find(_.accession == IMZML_EXTERNAL_ENCODED_LENGTH_ACC)
				if (extLength.isDefined && extOffset.isDefined && extEncLength.isDefined) {
					binaryByteChannel.position(extOffset.get.value.get.toLong)
					val encLength = extEncLength.get.value.get.toInt
					x.extLength = extLength.get.value.get.toInt
					x.encodedLength = encLength
					x.extBinary = ByteBuffer.allocate(encLength)
					binaryByteChannel.read(x.extBinary)
				}
			}
			case None => {}
		}
		x
	}
}

class BinaryDataArray {
	var arrayLength:Option[Int] = None
	var dataProcessingRef:Option[String] = None
	var encodedLength:Int = -1
	
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	var paramGroupRefs = new ArrayBuffer[String]
	var binary:String = null
	var extBinary:ByteBuffer = null
	var extLength:Int = 0
	
	
	def write(w:XmlWriter, binaryByteChannel:FileChannel) = {
		import MzML._
		import IMzML._
		
		if (binary != null && (extBinary != null || extLength > 0))
			throw new Exception("Two binary representations to choose from, can't decide which one to write!")
		if (extBinary != null && extLength > 0) {
			cvParams += new CvParam {
				cvRef = "IMS"
				accession = IMZML_EXTERNAL_DATA_ACC
				name = "external data"
				value = Some("true")
			}
			cvParams += new CvParam {
				cvRef = "IMS"
				accession = IMZML_EXTERNAL_ARRAY_LENGTH_ACC
				name = "external array length"
				value = Some(extLength.toString)
			}
			cvParams += new CvParam {
				cvRef = "IMS"
				accession = IMZML_EXTERNAL_OFFSET_ACC
				name = "external offset"
				value = Some(binaryByteChannel.position.toString)
			}
			cvParams += new CvParam {
				cvRef = "IMS"
				accession = IMZML_EXTERNAL_ENCODED_LENGTH_ACC
				name = "external encoded length"
				value = Some(encodedLength.toString)
			}
		}
		
		w.startElement(BINARY_DATA_ARRAY)
		w.writeAttribute(ENCODED_LENGTH, encodedLength)
		w.writeOptional(DATA_PROCESSING_REF, dataProcessingRef)
		for (x <- paramGroupRefs) ReferenceableParamGroupRef.write(w, x)
		for (x <- cvParams) x.write(w)
		for (x <- userParams) x.write(w)
		
		w.startElement(BINARY)
		if (binary != null)
			w.text(binary, false)
		else if (extBinary != null && extLength > 0)
			binaryByteChannel.write(extBinary)
		
		w.endElement
		w.endElement
	}
}