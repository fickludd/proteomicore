package se.lth.immun.xml

import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before

import java.io.StringReader
import java.io.IOException

class XmlReaderTest extends AssertionsForJUnit {

	val XML = """
			<book>
				<author title="Mrs" name="T. Bony" age="54" weight="63.2" />
				<title>A very long book.</title>
			</book>
		
		"""
	
	var r:XmlReader = null;
	
	@Before
	def setupReader() = {
		
		r = new XmlReader(new StringReader(XML))
	}
	
	@Test
	def readElement() = {
		
		if (!r.is("book")) fail("Couldn't read 'book' element")
		if (!r.is("author")) fail("Couldn't read 'author' element")
		assertFalse(r.is("author"))
		if (!r.is("title")) fail("Couldn't read 'title' element")
	}
	
	@Test
	def readElementText() = {
		
		r.is("book")
		r.is("author")
		r.is("title")
		assertEquals("A very long book.", r.text)
	}
	
	@Test
	def testStartBool() = {
		
		r.is("book")
		assertTrue(r.top.start)
		assertFalse(r.top.end)
		r.is("author")
		assertTrue(r.top.start)
		assertTrue(r.top.end)
		r.is("title")
		assert(r.atEOF)
	}
	
	@Test
	def accept() = {
		
		r.until("author")
		assertEquals("Mrs", r.readAttributeString("title"))
	}
	
	@Test
	def readAttributeString() = {
		
		r.until("author")
		assertEquals("Mrs", r.readAttributeString("title"))
	}
	
	@Test
	def readAttributeStringWithBlanks() = {
		
		r.until("author")
		assertEquals("T. Bony", r.readAttributeString("name"))
	}
	
	@Test
	def readAttributeInt() = {
		
		r.until("author")
		assertEquals("54", r.readAttributeString("age"))
		assertEquals(54, r.readAttributeInt("age"))
	}
	
	@Test
	def readAttributeDouble() = {
		
		r.until("author")
		assertEquals("63.2", r.readAttributeString("weight"))
		assertEquals(63.2, r.readAttributeDouble("weight"), 0.00001)
	}
	
	
	
	val XMLlist = """
			<!-- list of books -->
			<books>
				<book title="World's End" />
				<book title="Kafka and the world" ></book>
				<book title="Cooking across the centuries" />
				<bible />
			</books>
		"""
	
	@Test
	def skipComment = {
		r = new XmlReader(new StringReader(XMLlist))
		if (!r.is("books")) fail("Failed to skip comment")
		if (r.top.name != "books") fail("Failed to skip comment")
	}
	
	@Test
	def readList = {
		r = new XmlReader(new StringReader(XMLlist))
		var l = List[String]()
		r.is("books")
		var books = r.top
		while (r.nextIn(books)) {
			if (r.is("book"))
				l = l :+ r.readAttribute("title")
			else r.next
		}
		assertEquals("World's End", l(0))
		assertEquals("Kafka and the world", l(1))
		assertEquals("Cooking across the centuries", l(2))
	}
	
	@Test
	def ensure = {
		r = new XmlReader(new StringReader(XMLlist))
		r.ensure("books")
		assert(r.is("book"))
		assertEquals("World's End", r.readAttribute("title"))
		try {
			r.ensure("bible")
			fail("Didn't throw exception for violated ensure()")
		} catch {
			case e:IOException => 
				assertEquals("[line 4] Corrupt xml file: expected 'bible' tag, got 'book'", e.getMessage)
			case _:Throwable => fail("Thew wrong kind of exception")
		}
	}
	
	@Test
	def skipThis = {
		r = new XmlReader(new StringReader(XMLlist))
		r.is("books")
		r.skipThis
		assert(r.atEOF)
	}
	
	@Test
	def skip = {
		r = new XmlReader(new StringReader(XMLlist))
		r.is("books")
		r.skip("book")
		r.is("book")
		assertEquals("Kafka and the world", r.readAttribute("title"))
	}
	
	@Test
	def skipAll = {
		r = new XmlReader(new StringReader(XMLlist))
		r.is("books")
		r.skipAll("book")
		assert(r.is("bible"))
	}
	
	@Test
	def exit = {
		r = new XmlReader(new StringReader(XMLlist))
		r.is("books")
		var e = r.top
		r.exit(e)
		assert(r.atEOF)
	}
	
	@Test
	def buff = {
		r = new XmlReader(new StringReader(XMLlist))
		r.is("books")
		var e = r.top
		r.saveToBuff = true
		r.is("book")
		r.saveToBuff = false
		r.exit(e)
		assertEquals("""<book title="World's End" />
				""", r.getBuff)
	}
	
	@Test
	def buffAll = {
		r = new XmlReader(new StringReader(XMLlist), true)
		r.is("books")
		var e = r.top
		r.exit(e)
		assertEquals(XMLlist, r.getBuff)
	}
	/*
	
	@Test
	def acceptAlternatives() = {
		
		var l = List("title", "author")
		r.accept(l)
		assertEquals("author", r.elementName)
		r.accept(l)
		assertEquals("title", r.elementName)
	}
	
	@Test
	def reject() = {
		
		assertTrue(r.reject("author"))
		assertFalse(r.reject("author"))
		assertTrue(r.reject("author"))
	}
	
	@Test
	def rejectAlternatives() = {
		
		var l = List("title", "author")
		assertTrue(r.reject(l))
		assertFalse(r.reject(l))
		assertFalse(r.reject(l))
		assertFalse(r.reject(l))
		assertTrue(r.reject(l))
	}
	
	
	*/
}