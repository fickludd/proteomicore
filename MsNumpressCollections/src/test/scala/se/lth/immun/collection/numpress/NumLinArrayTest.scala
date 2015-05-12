package se.lth.immun.collection.numpress

import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before

import scala.util.Random

import ms.numpress.MSNumpress

class NumLinArrayTest {

	def assertSeq(target:Seq[Double], a:Iterable[Double]) = {
		val zipped = target.zip(a)
		assertEquals(zipped.length, target.length)
		val relDiffs = zipped.map(t => (t._1 - t._2) / ((t._1 + t._2)/2))
		relDiffs.foreach(rd => assertEquals(0.0, rd, 0.000005))
	}

	def assertBytes(target:Seq[Byte], a:Iterable[Byte]) = {
		val zipped = target.zip(a)
		zipped.map(t => assertEquals(t._1, t._2))
	}
	

	def diff(target:List[Byte], a:List[Byte]):List[Byte] = {
		if (a.isEmpty || target.isEmpty)
			return Nil
		
		if (target.head == a.head)
			diff(target.tail, a.tail)
		else
			target.head :: diff(target.tail, a)
	}
	
	
	
	@Test
	def empty() = {
		val a = new NumLinArray
		assert(a.isEmpty)
		
		val bytes = a.bytes.toArray
		val res = new Array[Double](4)
		assertEquals(0, MSNumpress.decodeLinear(bytes, bytes.length, res))
		
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
		
		val fp = 100000.0
		val a = new NumLinArray(fp)
		for (d <- mzs) a += d
		
		val encoded = a.bytes.toArray
		assertEquals(18, encoded.length)
		val dec_fp = MSNumpress.decodeFixedPoint(encoded)
		assertEquals(fp, dec_fp, 0.0)
		//assertEquals(18, c.i)
		assertEquals(0x80, 0xff & encoded(8))
		assertEquals(0x96, 0xff & encoded(9))
		assertEquals(0x98, 0xff & encoded(10))
		assertEquals(0x00, 0xff & encoded(11))
		assertEquals(0x75, 0xff & encoded(16))
		assertEquals(0x80, 0xf0 & encoded(17))
	}
	
	@Test
	def encodeOrigValidation() = {
		val n = 100
		val fp = 100000.0
		val a = new NumLinArray(fp)
		val mzs = new Array[Double](n);
		mzs(0) = 300 + Random.nextDouble
		for ( i <- 1 until n)
			mzs(i) = mzs(i-1) + Random.nextDouble
		
		for (d <- mzs) a += d
		
		
		val bytes = a.bytes.toArray
		
		val refBytes = new Array[Byte](8+5*n)
		val nRefBytes = MSNumpress.encodeLinear(mzs.toArray, n, refBytes, fp)
		assertEquals(nRefBytes, bytes.length)
		assertBytes(refBytes, bytes)
		
		val res = new Array[Double](n)
		assertEquals(n, MSNumpress.decodeLinear(bytes, bytes.length, res))
		assertSeq(mzs, res)
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