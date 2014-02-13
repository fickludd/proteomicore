package se.lth.immun.proteomics

object PeptideIon {
	
	object Order extends Ordering[PeptideIon] {
		def compare(a: PeptideIon, b: PeptideIon) = 
			if (a.sequence == b.sequence) {
				if (a == b) 0
				else a.mz.d compare b.mz.d
			} else a.sequence compare b.sequence
	}
	
	def Set(vd:PeptideIon*) = scala.collection.immutable.TreeSet(vd:_*)(Order)
	def Map[V](vd:(PeptideIon, V)*) = scala.collection.immutable.TreeMap(vd:_*)(Order)
}

case class PeptideIon(
		val mz:Mz,
		val sequence:String
) extends Equals {
	
	override def toString = sequence + " " + mz
	
	override def canEqual(that:Any):Boolean = that match {
		case other:PeptideIon => true
		case _ => false
	}
	
	override def equals(that:Any):Boolean = that match {
		case PeptideIon(omz, oseq) => omz == mz && oseq == sequence
		case _ => false
	}
	
	override def hashCode():Int = sequence.hashCode + mz.hashCode
}