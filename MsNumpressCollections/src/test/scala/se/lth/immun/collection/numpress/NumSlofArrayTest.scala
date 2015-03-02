package se.lth.immun.collection.numpress

import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before

import scala.util.Random

class NumSlofArrayTest {

	
	def assertSeq(target:Seq[Double], a:Iterable[Double]) = {
		val zipped = target.zip(a)
		assertEquals(zipped.length, target.length)
		val relDiffs = zipped.map(t => (t._1 - t._2) / ((t._1 + t._2)/2))
		relDiffs.foreach(rd => assertEquals(0.0, rd, 0.0005))
	}
	
	
	@Test
	def empty() = {
		val a = new NumSlofArray
		assert(a.isEmpty)
		
	}
	
	@Test
	def addValues() = {
		val a = new NumSlofArray
		
		for (i <- 0 until 100) a += math.pow(10, Random.nextDouble*100)
	}
	
	@Test
	def get100Values() = {
		val in = (0 until 100).map(_ => math.pow(10, Random.nextDouble*6))
		
		val a = new NumSlofArray(NumSlofArray.optimalFixedPoint(in.max))
		for (d <- in) a += d
		
		assertEquals(a.length, 100)
			
		assertSeq(in, a)
	}
	
	@Test
	def changeReadAHead() = {
		val in = (0 until 100).map(_ => math.pow(10, Random.nextDouble*6))
		
		val a = new NumSlofArray(NumSlofArray.optimalFixedPoint(in.max), 11)
		for (d <- in) a += d
		
		assertEquals(a.length, 100)
				
		assertSeq(in, a)
	}
	
	@Test
	def manyValues() = {
		val n = 10000
		val in = (0 until n).map(_ => math.pow(10, Random.nextDouble*6))
		
		val a = new NumSlofArray(NumSlofArray.optimalFixedPoint(in.max), 11)
		for (d <- in) a += d
		
		assertEquals(a.length, n)
		
		assertSeq(in, a)
	}
	
	
}