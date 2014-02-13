package se.lth.immun.traml.ghost

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap
import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter

import se.lth.immun.traml._

object GhostTraML {
	
	def fromFile(r:XmlReader):GhostTraML = {
		var x = new GhostTraML
		
		x.traml = TraML.fromFile(r)
		for (p <- x.traml.proteins)
			x.proteins += p.id -> GhostProtein.fromProtein(p)
		for (p <- x.traml.compoundList.peptides)
			x.peptides += p.id -> GhostPeptide.fromPeptide(p)
		for (p <- x.traml.compoundList.compounds)
			x.compounds += p.id -> GhostCompound.fromCompound(p)
		
		for (t <- x.traml.transitions) {
			var gt = GhostTransition.fromTransition(t)
			x.transitions += gt
			val key = new Tuple2(gt.q1, gt.peptideRef)
			if (!x.transitionGroups.contains(key))
				x.transitionGroups += key -> new ArrayBuffer[GhostTransition]
			x.transitionGroups(key) += gt
		}
		
		return x
	}
}


class GhostTraML {

	var traml:TraML = null
	val proteins = new HashMap[String, GhostProtein]
	val peptides = new HashMap[String, GhostPeptide]
	val compounds = new HashMap[String, GhostCompound]
	val transitions = new ArrayBuffer[GhostTransition]
	val transitionGroups = new HashMap[(Double, String), ArrayBuffer[GhostTransition]]
	
	
	
	def write(w:XmlWriter) = {
		if (traml == null) traml = new TraML
		
		traml.proteins.clear
		for (t <- proteins)
			traml.proteins +=  t._2.toProtein
			
		if (traml.compoundList == null) traml.compoundList = new CompoundList
		traml.compoundList.peptides.clear
		for (t <- peptides)
			traml.compoundList.peptides +=  t._2.toPeptide
			
		traml.compoundList.compounds.clear
		for (t <- compounds)
			traml.compoundList.compounds +=  t._2.toCompound
		
		traml.transitions.clear
		for (t <- transitions)
			traml.transitions += t.toTransition
			
		traml.write(w)
	}
}