package se.lth.immun.traml.clear

import scala.collection.mutable.ArrayBuffer
import Clear._

abstract class ClearCompound {
	def id:String
	val assays = new ArrayBuffer[Assay]
	var rt:Option[ClearRetentionTime.RT] = None
	def getAssay(mz:Double, z:Int, ce:Option[Double]) =
		assays.find(_.z == z) match {
			case Some(a) => a
			case None =>
				val a = new Assay(mz, z, id, ce)
				assays += a
				a
		}
	/*
	def add(aInfo:AssayInfo, c:Channel) = {
		assays.find(_.z == aInfo.q1z) match {
			case Some(assay) =>
				if (c.msLevel == 1)
					assay.ms1Channels += c
				else if (c.msLevel == 2) {
					assay.mz = assay.mz.orElse(Some(aInfo.q1))
					assay.ms2Channels += c
				} else 
					throw new Exception("Unsupported ms level %d of transition %s".format(c.msLevel, c.id))
			case None =>
				val a = new Assay(id+"_"+aInfo.q1z, Some(aInfo.q1), aInfo.q1z, aInfo.ce)
				if (c.msLevel == 1) 		a.ms1Channels += c
				else if (c.msLevel == 2)	a.ms2Channels += c
				assays += a
		}
	}*/
	override def equals(o:Any) = 
		o match {
			case that: ClearCompound => that.id == this.id
		    case _ => false
		}
	override def hashCode = id.hashCode
}