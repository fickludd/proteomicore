package se.lth.immun.math

import scala.collection.immutable.TreeSet
import scala.collection.immutable.TreeMap

object VagueDoubleImplicits {
	class VagueDoubleHelper(d: Double, uncertainty: Double) {
		def vd = new VagueDouble(d, uncertainty)
		def u(uncertainty: Double) = new VagueDouble(d, uncertainty)
	}

	implicit def double2vagueDouble(d: Double)(implicit uncertainty: Double) = new VagueDouble(d, uncertainty)
	implicit def doubleWrapper(d: Double)(implicit uncertainty: Double): VagueDoubleHelper = new VagueDoubleHelper(d, uncertainty)
}

object VagueDouble {

	def apply(d: Double)(implicit uncertainty: Double) = new VagueDouble(d, uncertainty)
	def unapply(vd: VagueDouble): Option[(Double, Double)] = Some((vd.d, vd.uncertainty))

	object Order extends Ordering[VagueDouble] {
		def compare(a: VagueDouble, b: VagueDouble) = 
			if (a == b) 0
			else a.d compare b.d
	}
	
	def Set(vd:VagueDouble*) = TreeSet(vd:_*)(Order)
	def Map[V](vd:(VagueDouble, V)*) = TreeMap(vd:_*)(Order)
}

class VagueDouble(
	val d: Double,
	val uncertainty: Double = 0.0) extends Equals {
	override def toString = "%.3f+-%.3f".format(d, uncertainty)

	override def canEqual(that: Any): Boolean = that match {
		case other: VagueDouble => true
		case _ => false
	}

	override def equals(that: Any): Boolean = that match {
		case VagueDouble(od, ou) =>
			math.abs(d - od) < math.max(uncertainty, ou)
		case _ => false
	}

	override def hashCode(): Int = {
		throw new Exception("WARNING HASD TROUBLE! For collections use VagueDouble.Set or VagueDouble.Map")
		d.hashCode + uncertainty.hashCode
	}
}