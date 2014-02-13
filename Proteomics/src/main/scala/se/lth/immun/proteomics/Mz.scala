package se.lth.immun.proteomics

import scala.collection.immutable.TreeSet
import scala.collection.immutable.TreeMap

object MzImplicits {
	class MzHelper(d: Double, uncertainty: Double) {
		def vd = new Mz(d, uncertainty)
		def u(uncertainty: Double) = new Mz(d, uncertainty)
	}

	implicit def double2Mz(d: Double)(implicit uncertainty: Double) = new Mz(d, uncertainty)
	implicit def doubleWrapper(d: Double)(implicit uncertainty: Double): MzHelper = new MzHelper(d, uncertainty)
}

object Mz {

	def apply(d: Double)(implicit uncertainty: Double) = new Mz(d, uncertainty)
	def unapply(vd: Mz): Option[(Double, Double)] = Some((vd.d, vd.uncertainty))

	object Order extends Ordering[Mz] {
		def compare(a: Mz, b: Mz) = 
			if (a == b) 0
			else a.d compare b.d
	}
	
	def Set(vd:Mz*) = TreeSet(vd:_*)(Order)
	def Map[V](vd:(Mz, V)*) = TreeMap(vd:_*)(Order)
}

class Mz(
	val d: Double,
	val uncertainty: Double = 0.0) extends Equals {
	override def toString = "%.3f+-%.3f".format(d, uncertainty)

	override def canEqual(that: Any): Boolean = that match {
		case other: Mz => true
		case _ => false
	}

	override def equals(that: Any): Boolean = that match {
		case Mz(od, ou) =>
			math.abs(d - od) < math.max(uncertainty, ou)
		case _ => false
	}

	override def hashCode(): Int = {
		throw new Exception("WARNING HASD TROUBLE! For collections use Mz.Set or Mz.Map")
		d.hashCode + uncertainty.hashCode
	}
}