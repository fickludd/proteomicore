package graphs.util

import org.scalatest.junit.AssertionsForJUnit

import scala.collection.mutable.ListBuffer
import se.lth.immun.graphs.util.DateAxis;

import org.junit.Assert._
import org.junit.Test
import org.junit.Before

import java.util.Date
import java.util.Calendar

class DateAxisTest extends AssertionsForJUnit {

	val cal = Calendar.getInstance
	cal.clear
	cal.set(2011, 9, 20, 10, 31, 21)
	
	var dates = List(cal.getTime())
	cal.set(2011, 9, 23, 1, 1, 1)
	dates = dates :+ cal.getTime
	cal.set(2011, 10, 4, 23, 45, 59)
	dates = dates :+ cal.getTime
	cal.set(2011, 10, 5, 14, 59, 59)
	dates = dates :+ cal.getTime
	
	
	@Test
	def emptyAxis = {
		var da = new DateAxis(new Date, new Date)
		for (d <- dates) {
			assertFalse(da.isVisible(d))
			assertTrue(java.lang.Double.isNaN(da.x2gx(d)))
		}
	}
	
	@Test
	def simple = {
		var da = new DateAxis(dates(0), dates(3))
		for (d <- dates) {
			assertTrue(da.isVisible(d))
			assertFalse(java.lang.Double.isNaN(da.x2gx(d)))
			assertEquals(d, da.gx2x(da.x2gx(d)))
		}
	}
	
	@Test
	def zoom = {
		var da = new DateAxis(new Date, new Date)
		
		da.zoom(new Date(dates(2).getTime - 1000), new Date(dates(2).getTime + 1000))
		assertTrue(da.isVisible(dates(2)))
		assertFalse(da.isVisible(dates(0)))
		assertFalse(da.isVisible(dates(1)))
		assertFalse(da.isVisible(dates(3)))
		assertEquals(0.5, da.x2gx(dates(2)), 0.0001)
		
		
		da.zoom(dates)
		for (d <- dates) {
			assertTrue(da.isVisible(d))
			assertFalse(java.lang.Double.isNaN(da.x2gx(d)))
			assertEquals(d, da.gx2x(da.x2gx(d)))
		}
	}
}