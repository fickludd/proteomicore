package graphs.util

import org.scalatest.junit.AssertionsForJUnit

import scala.collection.mutable.ListBuffer
import se.lth.immun.graphs.util.LogAxis;

import org.junit.Assert._
import org.junit.Test
import org.junit.Before

class LogAxisTest extends AssertionsForJUnit {

	var vals = List(math.E, 4.0, 6.0, math.E * math.E)
	
	
	@Test
	def emptyAxis = {
		try {
			var a = new LogAxis(0.0, 0.0)
			fail("Didn't throw exception for '0.0' value")
		} catch {
			case e:Exception => {}
		}
		
		var la = new LogAxis(0.01, 1.0)
		for (v <- vals) {
			assertFalse(la.isVisible(v))
			assertTrue(java.lang.Double.isNaN(la.x2gx(v)))
		}
	}
	
	@Test
	def simple = {
		var la = new LogAxis(vals(0), vals(3))
		for (v <- vals) {
			assertTrue(la.isVisible(v))
			assertFalse(java.lang.Double.isNaN(la.x2gx(v)))
			assertEquals(v, la.gx2x(la.x2gx(v)), 0.0001)
		}
	}
	
	@Test
	def zoom = {
		var la = new LogAxis(vals(0), vals(3))
		
		la.zoom(math.pow(10, math.log10(vals(1)) - 0.01), math.pow(10, math.log10(vals(1)) + 0.01))
		assertTrue(la.isVisible(vals(1)))
		assertFalse(la.isVisible(vals(0)))
		assertFalse(la.isVisible(vals(2)))
		assertFalse(la.isVisible(vals(3)))
		assertEquals(0.5, la.x2gx(vals(1)), 0.0001)
		
		
		la.zoom(vals)
		for (v <- vals) {
			assertTrue(la.isVisible(v))
			assertFalse(java.lang.Double.isNaN(la.x2gx(v)))
			assertEquals(v, la.gx2x(la.x2gx(v)), 0.0001)
		}
	}
}