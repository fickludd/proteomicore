package se.lth.immun.proteomics

import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

import scala.collection.immutable.TreeSet

@RunWith(classOf[JUnitRunner])
class MzTest extends FunSuite {

	test ("simpleEquals") {
		val a = new Mz(1.0, 0.1)
		val b = new Mz(2.0, 0.95)
		val c = new Mz(2.5, 0.0)
		
		assert(a != b)
		assert(b === c)
		assert(a != c)
	}
	
	test ("implicitness") {
		implicit val uncertainty = 0.3
		
		val a = Mz(2.5)
		val b = Mz(2.7)
		
		assert(a === b)
	}
	
	test ("setOperations") {
		implicit val uncertainty = 0.3
		
		val s1 = Mz.Set(Mz(1.0), Mz(2.0), Mz(7.0), Mz(7.1003), Mz(12.0))
		assert(4 === s1.size)
		
		val s2 = Mz.Set(Mz(1.5), Mz(2.2), Mz(8.0), Mz(12.3))
		assert(4 === s2.size)
		
		val intersection = s1 intersect s2
		assert(intersection contains Mz(2.0), "intersect should containt (2.0 +- 0.3)")
		
		val union = s1 union s2
		assert(union contains Mz(1.0), "union should containt (1.0 +- 0.3)")
		assert(union contains Mz(1.5), "union should containt (1.5 +- 0.3)")
		assert(union contains Mz(2.0), "union should containt (2.0 +- 0.3)")
		assert(union contains Mz(7.0), "union should containt (7.0 +- 0.3)")
		assert(union contains Mz(8.0), "union should containt (8.0 +- 0.3)")
		assert(union contains Mz(12.0), "union should containt (12.0 +- 0.3)")
		assert(union contains Mz(12.3), "union should containt (12.3 +- 0.3)")
	}
	
	test ("superImplicitness") {
		implicit val uncertainty = 0.5
		import MzImplicits._
		
		val a = 2.5 vd
		val b:Mz = 2.9
		val c = 4.0 u 2.0
		
		assert(a === b)
		assert(a === c)
	}
}