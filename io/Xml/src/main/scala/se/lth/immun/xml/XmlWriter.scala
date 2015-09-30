package se.lth.immun.xml

import java.io.Writer
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.OutputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.GZIPOutputStream
import java.security.DigestOutputStream
import java.security.MessageDigest

object XmlWriter {
	def apply(file:File, gzip:Boolean, bufferSize:Int = 4096) = {
		val os = 
			if (gzip) new GZIPOutputStream(new FileOutputStream(file))
			else new FileOutputStream(file)
		val sha1 = MessageDigest.getInstance("SHA-1")
		val sha1os = new DigestOutputStream(os, sha1)
		val cos = new CountingOutputStream(sha1os)
		new XmlWriter(
				new OutputStreamWriter(cos), 
				cos.count _,
				() => sha1.digest.map(b => "%02x".format(b)).mkString,
				bufferSize
			)
	}
	
	def apply(w:Writer) =
		new XmlWriter(w, () => 0L, () => "")
	
	type ChecksumType = String
	type Checksum = String
}

class XmlWriter(
		writer:Writer, 
		underlyingByteOffset:() => Long, 
		underlyingChecksum: () => XmlWriter.Checksum, 
		bufferSize:Int = 4096
) {
		
	var bw = new BufferedWriter(writer, bufferSize)
	private var elementStack = List[String]()
	private var lastElement = ""
	
	def byteOffset:Long = {
		bw.flush
		underlyingByteOffset()
	}
	
	def checksum:String = {
		bw.flush
		underlyingChecksum()
	}
	
	var lastStartByteOffset:Option[Long] = None
	var storeNext = false
	def storeNextElementStartByteOffset =
		storeNext = true
		
	
		
	def write(str:String):Unit = bw.write(str)
	
	def text(text:String, newlines:Boolean = true) = {
    	if (newlines) {
    		if (elementStack.head == lastElement)
    			bw.write(">\n")
	    	lastElement = ""
	    	bw.write(text)
	    	if (text.length > 0 && text.last != '\n') 
	    		bw.write("\n")
    	} else {
    		if (elementStack.head == lastElement)
    			bw.write(">")
    		lastElement = null
    		bw.write(text)
    	}
    }
	
	def startDocument = 
		bw.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n")
	
	
	def endDocument = {
		bw.flush
		bw.close
	}

    def writeAttribute[T](attributeName:String, value:T) =
        bw.write(" "+attributeName+"=\""+value+"\"")

    def writeOptional[T](attributeName:String, opt:Option[T]) =
        if (opt.isDefined)
        	bw.write(" "+attributeName+"=\""+opt.get+"\"")
    
    def startElement(element:String) = {
    	if (!elementStack.isEmpty && elementStack.head == lastElement)
    		closeStartElement
    	if (storeNext) {
    		bw.write("  " * elementStack.length)
    		lastStartByteOffset = Some(byteOffset)
    		storeNext = false
    		bw.write("<"+element)
    	} else
    		bw.write("  " * elementStack.length + "<"+element)
    	elementStack = element :: elementStack
    	lastElement = element
    }
    
    def closeStartElement = {
    	bw.write(">\n")
    	lastElement = ""
    }
    
    def endStartElement = {
    	bw.write("/>\n")
    	lastElement = ""
    }
    
    def endElement() = {
    	if (elementStack.isEmpty) throw new IOException("No start element to end!")
    	if (elementStack.head == lastElement)
    		endStartElement
    	else if (lastElement == null) {
    		bw.write("</"+elementStack.head+">\n")
    		lastElement = ""
    	} else
    		bw.write("  " * elementStack.tail.length + "</"+elementStack.head+">\n")

    	elementStack = elementStack.tail
    }
    
    def startListElement[T](element:String, arr:Seq[T]) = {
        startElement(element)
        writeAttribute(XmlUtil.COUNT, arr.length)
    }
}