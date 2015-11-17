package se.lth.immun.traml.clear

import scala.collection.mutable.ArrayBuffer
import se.lth.immun.traml.Peptide
import Clear._

class ClearPeptide(
		val id:String,
		proteinRefs:Seq[String]
) extends ClearCompound {
	val proteins = new ArrayBuffer[String]
	proteins ++= proteinRefs
	def seq = id 
	def metaCopy = {
		val cp = new ClearPeptide(id, proteinRefs)
		cp.rt = rt
		cp
	}
}