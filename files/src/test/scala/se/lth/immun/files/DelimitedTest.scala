package se.lth.immun.files

import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before

import java.io.BufferedReader
import java.io.StringReader

class DelimitedTest extends AssertionsForJUnit {
	
	@Test 
	def csvEmptyLine() = {
		var words = Delimited.readRow(',', '"', "")
		assertEquals(words.length, 0)
	}
	
	@Test
	def csvEasyLine() = {
		var words = Delimited.readRow(',', '"', "A, B, C,D,  F")
		assertEquals(words.length, 5)
		assertEquals(words(0), "A")
		assertEquals(words(1), "B")
		assertEquals(words(2), "C")
		assertEquals(words(3), "D")
		assertEquals(words(4), "F")
	}
	
	@Test
	def csvComplexLine() = {
		var words = Delimited.readRow(',', '"', """A, "B,,,C", D""")
		assertEquals(words(0), "A")
		assertEquals(words(1), "B,,,C")
		assertEquals(words(2), "D")
	}
	
	@Test
	def csvReader() = {
		var str = """A, B, C
1, 2,3 
4, 5, 6
"""
		var reader = new DelimitedReader(',', '"', new BufferedReader(new StringReader(str)), true)
		assertEquals("B", reader.tags(1))
		assertEquals("3", (reader.readRow)(2))
		assertEquals("4", (reader.readRow)(0))
	}
	
	@Test
	def csvSparseReader() = {
		var str = """A, B, C
1, 2,3 
,, 6
"""
		var reader = new SparseDelimitedReader(',', '"', new BufferedReader(new StringReader(str)))
		assertEquals("C", reader.tags(2))
		assertEquals("1", (reader.readRow())(0))
		var row = reader.readRow
		assertEquals("1", row(0))
		assertEquals("2", row(1))
		assertEquals("6", row(2))
	}
}
