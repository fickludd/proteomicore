package se.lth.immun.files

import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before

class IOTest extends AssertionsForJUnit {
	
	@Test
	def stripCorrect() = {
		assertEquals("book", IO.strip("book"))
		assertEquals("book", IO.strip(" \nbook\r\t"))
	}
	
	@Test
	def stripCustom() = {
		assertEquals(" book ", IO.strip("\" book \"", Array('"')))
	}
}
