package se.lth.immun.math

import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

import scala.util.Random

/*
 * Reference values calculated with 
 * http://elegans.swmed.edu/~leon/stats/utest.cgi
 */

@RunWith(classOf[JUnitRunner])
class StatsTest extends FunSuite {
	
	import Stats._
	
	var arr1:Array[Double] = null
	var arr2:Array[Double] = null
	var arr3:Array[Double] = null
	
	def setupArrays = {
		arr1 = Array()
		arr2 = Array(2.0)
		arr3 = Array(0.0, 0.0, 8.0, 2.0, 5.0)
	}
	
	test ("mean") {
		setupArrays
		assert(java.lang.Double.isNaN(mean(arr1)))
		assert(2.0 === mean(arr2))
		assert(3.0 === mean(arr3))
	}
	
	test ("median") {
		setupArrays
		val a = new Array[Double](100)
		val a4 = a.map(d => Random.nextDouble) ++ a.map(d => Random.nextDouble + 1.01) :+ 1.0
		
		assert(java.lang.Double.isNaN(median(arr1)))
		assert(2.0 === median(arr2))
		assert(2.0 === median(arr3))
		assert(1.0 === median(a4))
	}
	
	
	/*
	@Test
	def histogram = {
		var a = Array(1.0, 1.5, 2.0, 2.1, 2.2, 2.01, 2.99, 2.76, 3.5, 3.1, 3.0, 4.0)
		var hist = Histogram.fromArray(a, 3)
		
		assert(hist != null)
		assert(hist.bins != null)
		assert(hist.counts != null)
		
		assertEquals(3, hist.bins.length)
		assertEquals(3, hist.counts.length)
		
		assertEquals(1.5, hist.bins(0), 0.000001)
		assertEquals(2.5, hist.bins(1), 0.000001)
		assertEquals(3.5, hist.bins(2), 0.000001)
		
		assertEquals(2.0, hist.counts(0), 0.000001)
		assertEquals(6.0, hist.counts(1), 0.000001)
		assertEquals(4.0, hist.counts(2), 0.000001)
	}
	
	@Test
	def histogramLog = {
		var a = Array(1.0, 1.5, 1.99, 2.1, 2.2, 2.0, 2.99, 2.76, 3.5, 3.1, 3.0, 4.0)
		var hist = Histogram.fromArray(a, 3, true)
		
		assert(hist != null)
		assert(hist.bins != null)
		assert(hist.counts != null)
		
		assertEquals(3, hist.bins.length)
		assertEquals(3, hist.counts.length)
		
		assertEquals(1.2937, hist.bins(0), 0.000001)
		assertEquals(2.053621576, hist.bins(1), 0.000001)
		assertEquals(3.25992105, hist.bins(2), 0.000001)
		
		assertEquals(2.0, hist.counts(0), 0.000001)
		assertEquals(4.0, hist.counts(1), 0.000001)
		assertEquals(6.0, hist.counts(2), 0.000001)
	}
	
	@Test
	def histogramLogZeros = {
		var a = Array(-1.0, 0.0, 1.0, 1.5, 1.99, 2.1, 2.2, 2.0, 2.99, 2.76, 3.5, 3.1, 3.0, 4.0)
		var hist = Histogram.fromArray(a, 3, true)
		
		assertEquals(0.2937, hist.bins(0), 0.000001)
		assertEquals(2.053621576, hist.bins(1), 0.000001)
		assertEquals(3.25992105, hist.bins(2), 0.000001)
		
		assertEquals(4.0, hist.counts(0), 0.000001)
		assertEquals(4.0, hist.counts(1), 0.000001)
		assertEquals(6.0, hist.counts(2), 0.000001)
	}
	
	@Test
	def histogramLogError = {
		var a = Array(-1.0, 0.0, -2.0, 0.0)
		try {
			var hist = Histogram.fromArray(a, 3, true)
			
		} catch {
			case e:IllegalArgumentException => {}
			case e:Exception => fail("Threw wrong kind of exception on "+
					"invalid input, expected IllegalArgumentException, got '"+e+"'")
		}
	}
	
	@Test
	def quantileogram = {
		var a = Array(1.0, 1.5, 2.0, 2.1, 2.2, 2.01, 2.99, 2.76, 3.5, 3.1, 3.0, 4.0)
		var q = Quantileogram.fromArray(a, 3)
		
		assert(q != null)
		assert(q.quantMid != null)
		
		assertEquals(3, q.quantMid.length)
		
		assertEquals(1.6275, q.quantMid(0), 0.000001)
		assertEquals(2.5125, q.quantMid(1), 0.000001)
		assertEquals(3.4, q.quantMid(2), 0.000001)
	}
	*/
}