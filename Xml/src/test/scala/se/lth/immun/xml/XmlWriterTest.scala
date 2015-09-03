package se.lth.immun.xml

import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before

import java.io.StringWriter

class XmlWriterTest extends AssertionsForJUnit {
	
	var s:StringWriter = null
	var w:XmlWriter = null
	
	@Before
	def setupWriter = {
		s = new StringWriter
		w = new XmlWriter(s)
	}
	
	@Test
	def writeElement = {
		w.startElement("book")
		w.closeStartElement
		w.endDocument
		assertEquals("<book>\n", s.toString)
	}
	
	@Test
	def writeStartEndElement = {
		w.startElement("book")
		w.endElement
		w.endDocument
		assertEquals("<book/>\n", s.toString)
	}
	
	@Test
	def writeEndElement = {
		w.startElement("h")
		w.startElement("book")
		w.startElement("author")
		w.text("hepp")
		w.endElement
		w.endElement
		w.endElement
		w.endDocument
		assertEquals("<h>\n  <book>\n    <author>\nhepp\n    </author>\n  </book>\n</h>\n", s.toString)
	}
	
	@Test
	def writeRawTextElement = {
		w.startElement("h")
		w.startElement("book")
		w.startElement("author")
		w.text("hepp", false)
		w.endElement
		w.endElement
		w.endElement
		w.endDocument
		assertEquals("<h>\n  <book>\n    <author>hepp</author>\n  </book>\n</h>\n", s.toString)
	}
	
	@Test
	def writeAttribute = {
		w.startElement("book")
		w.writeAttribute("title", "My book")
		w.writeAttribute("year", 2011)
		w.endElement
		w.endDocument
		assertEquals("<book title=\"My book\" year=\"2011\"/>\n", s.toString)
	}
	
	@Test
	def writeStartListElement = {
		w.startListElement("books", Array("A", "B", "C"))
		w.endElement
		w.endDocument
		assertEquals("<books count=\"3\"/>\n", s.toString)
	}
	
	@Test
	def writeStartDocument = {
		w.startDocument
		w.endDocument
		assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n", s.toString)
	}
}