package se.lth.immun.collection.numpress

import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before

import collection.mutable.ArrayBuffer

class NumpressUtilTest {

	
	
	@Test
	def encodeInt() {
		val res = new Array[Byte](10)
		var l = 0
		l = NumpressUtil.encodeInt(0l, res, 0)
		assertEquals(1, l)
		assertEquals(8, res(0) & 0xf)
		
		l = NumpressUtil.encodeInt(-1l, res, 0)
		assertEquals(2, l)
		assertEquals(0xf, res(0) & 0xf)
		assertEquals(0xf, res(1) & 0xf)
		
		l = NumpressUtil.encodeInt(35l, res, 0)
		assertEquals(3, l)
		assertEquals(6, res(0) & 0xf)
		assertEquals(0x3, res(1) & 0xf)
		assertEquals(0x2, res(2) & 0xf)
		
		l = NumpressUtil.encodeInt(370000000l, res, 0)
		assertEquals(9, l)
		assertEquals(0, res(0) & 0xf)
		assertEquals(0x0, res(1) & 0xf)
		assertEquals(0x8, res(2) & 0xf)
		assertEquals(0x0, res(3) & 0xf)
		assertEquals(0xc, res(4) & 0xf)
		assertEquals(0xd, res(5) & 0xf)
		assertEquals(0x0, res(6) & 0xf)
		assertEquals(0x6, res(7) & 0xf)
		assertEquals(0x1, res(8) & 0xf)
	}
	

	@Test
	def encodeFixedPoint() = {
		val encoded = new ArrayBuffer[Byte](8)
		NumpressUtil.encodeFixedPoint(1.00, encoded);
		assertEquals(0x3f, 0xff & encoded(0))
		assertEquals(0xf0, 0xff & encoded(1))
		assertEquals(0x0, 0xff & encoded(2))
		assertEquals(0x0, 0xff & encoded(3))
		assertEquals(0x0, 0xff & encoded(4))
		assertEquals(0x0, 0xff & encoded(5))
		assertEquals(0x0, 0xff & encoded(6))
		assertEquals(0x0, 0xff & encoded(7))
	}



	@Test
	def encodeDecodeFixedPoint() = {
		val fp = 300.21941382293625
		val encoded = new ArrayBuffer[Byte](8)
		NumpressUtil.encodeFixedPoint(fp, encoded)
		val decoded = NumpressUtil.decodeFixedPoint(encoded)
		assertEquals(fp, decoded, 0)
	}
}