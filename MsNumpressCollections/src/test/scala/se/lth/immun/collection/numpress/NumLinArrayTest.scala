package se.lth.immun.collection.numpress

import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before

import scala.util.Random

class NumLinArrayTest {

	def assertSeq(target:Seq[Double], a:Iterable[Double]) = {
		val zipped = target.zip(a)
		assertEquals(zipped.length, target.length)
		val relDiffs = zipped.map(t => (t._1 - t._2) / ((t._1 + t._2)/2))
		relDiffs.foreach(rd => assertEquals(0.0, rd, 0.000005))
	}
	
	
	@Test
	def empty() = {
		val a = new NumLinArray
		assert(a.isEmpty)
		
	}
	
	@Test
	def addValues() = {
		val n = 100
		val a = new NumLinArray
		val mzs = new Array[Double](n);
		mzs(0) = 300 + Random.nextDouble
		for ( i <- 1 until n)
			mzs(i) = mzs(i-1) + Random.nextDouble
		
		for (d <- mzs) a += d
	}
	
	@Test
	def encodeLinear() = {
		val mzs = Array(100.0, 200.0, 300.00005, 400.00010)
		val encoded 		= new Array[Byte](40)
		
		val a = new NumLinArray(100000.0)
		for (d <- mzs) a += d
		
		val i = a.iterator
		val c = a.ba.chunk
		//assertEquals(18, c.i)
		assertEquals(0x80, 0xff & c.a(8))
		assertEquals(0x96, 0xff & c.a(9))
		assertEquals(0x98, 0xff & c.a(10))
		assertEquals(0x00, 0xff & c.a(11))
		assertEquals(0x75, 0xff & c.a(16))
		assertEquals(0x80, 0xf0 & c.a(17))
	}
	
	
	@Test
	def get100Values() = {
		val n = 100
		val a = new NumLinArray
		val mzs = new Array[Double](n);
		mzs(0) = 300 + Random.nextDouble
		for ( i <- 1 until n)
			mzs(i) = mzs(i-1) + Random.nextDouble
		
		for (d <- mzs) a += d
		
		assertEquals(a.length, 100)
		
		assertSeq(mzs, a)
	}
	
	@Test
	def manyValues() = {
		val n = 10000
		val mzs = new Array[Double](n);
		mzs(0) = 300 + Random.nextDouble
		for ( i <- 1 until n)
			mzs(i) = mzs(i-1) + Random.nextDouble
		
		val a = new NumLinArray(NumLinArray.optimalFixedPoint(mzs), 11)
		for (d <- mzs) a += d
		
		assertEquals(a.length, n)
		
		assertSeq(mzs, a)
	}
}