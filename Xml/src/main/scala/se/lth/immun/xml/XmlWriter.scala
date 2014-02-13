package se.lth.immun.xml

import java.io.Writer
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.OutputStream

trait WithCount {
	def count:Long
}

object XmlWriter {
	def apply(outputStream:OutputStream, bufferSize:Int = 4096) = {
		val cos = new CountingOutputStream(outputStream)
		new XmlWriter(new OutputStreamWriter(cos), bufferSize) with WithCount {
			def count = {
				bw.flush
				cos.count
			}
		}
	}
}

class XmlWriter(writer:Writer, bufferSize:Int = 4096) {
		
	var bw = new BufferedWriter(writer, bufferSize)
	def byteCount:Long = 0
	private var elementStack = List[String]()
	private var lastElement = ""
	
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
	
	def startDocument() = {
		bw.write("<?xml version=\"1.0\"?>\n")
	}
	
	def endDocument() = {
		bw.flush()
		bw.close()
	}

    def writeAttribute[T](attributeName:String, value:T) =
        bw.write(" "+attributeName+"=\""+value+"\"")

    def writeOptional[T](attributeName:String, opt:Option[T]) =
        if (opt.isDefined)
        	bw.write(" "+attributeName+"=\""+opt.get+"\"")
    
    def startElement(element:String) = {
    	if (!elementStack.isEmpty && elementStack.head == lastElement)
    		closeStartElement()
    	bw.write("  " * elementStack.length + "<"+element)
    	elementStack = element :: elementStack
    	lastElement = element
    }
    
    def closeStartElement() = {
    	bw.write(">\n")
    	lastElement = ""
    }
    
    def endStartElement() = {
    	bw.write("/>\n")
    	lastElement = ""
    }
    
    def endElement() = {
    	if (elementStack.isEmpty) throw new IOException("No start element to end!")
    	if (elementStack.head == lastElement)
    		endStartElement()
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