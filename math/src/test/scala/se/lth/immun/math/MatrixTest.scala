package se.lth.immun.math

import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before

class JTMatrixTest extends AssertionsForJUnit {
	
	@Test
	def get2dSquare() = {
		val m = Matrix.get2d(3)
		assertEquals(3, m.length)
		for (row <- m)
			assertEquals(3, row.length)
	}
	
	@Test
	def get2d_3x4() = {
		val m = Matrix.get2d(3, 4)
		assertEquals(3, m.length)
		for (row <- m)
			assertEquals(4, row.length)
	}
	
	@Test
	def get2d_0x0() = {
		try {
			Matrix.get2d(0, 0)
			assertTrue(false)
		} catch {
			case iae:IllegalArgumentException => assertTrue(true)
			case _:Throwable => assertTrue(false)
		}
	}
}