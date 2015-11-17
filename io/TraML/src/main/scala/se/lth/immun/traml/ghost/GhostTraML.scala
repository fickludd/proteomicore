package se.lth.immun.traml.ghost

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap
import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter

import se.lth.immun.traml._

object GhostTraML {
	
	case class PrecKey(mz:Double, pepCompId:String) {
		override def toString = "%s_%.6f".format(pepCompId, mz)
		
	}
	
	def fromFile(r:XmlReader, repFreq:Int = 0):GhostTraML = {
		var x = new GhostTraML
		
		x.traml = TraML.fromFile(r, repFreq)
		for (p <- x.traml.proteins)
			x.proteins += p.id -> GhostProtein.fromProtein(p)
		for (p <- x.traml.compoundList.peptides)
			x.peptides += p.id -> GhostPeptide.fromPeptide(p)
		for (p <- x.traml.compoundList.compounds)
			x.compounds += p.id -> GhostCompound.fromCompound(p)
		
		val keyMap = new HashMap[(String, Int), PrecKey]
		for (t <- x.traml.transitions) {
			val gt = GhostTransition.fromTransition(t, x.peptides, x.compounds)
			keyMap((gt.pepCompId, gt.peptide.flatMap(_.charge).getOrElse(gt.q1z))) = 
				PrecKey(gt.q1, gt.pepCompId)
			x += gt
		}
		
		if (x.traml.targetList.isDefined) {
			val gts = 
				for (t <- x.traml.targetList.get.targetIncludes) yield 
					GhostTarget.fromTarget(t, x.peptides, x.compounds)
			for {
				((pepCompId, z), assayGts) <- gts.groupBy(gt => (gt.pepCompId, gt.q1z))
				gt <- assayGts
			} {
				val precKey = 
					keyMap.get((gt.pepCompId, gt.q1z))
						.getOrElse(
							PrecKey(assayGts.map(_.q1).min, gt.pepCompId)
						)
				x.add(precKey, gt)
			}
				
		}
		
		return x
	}
}


class GhostTraML {
	
	import GhostTraML._

	var traml:TraML = null
	val proteins = new HashMap[String, GhostProtein]
	val peptides = new HashMap[String, GhostPeptide]
	val compounds = new HashMap[String, GhostCompound]
	val transitions = new ArrayBuffer[GhostTransition]
	val transitionGroups = new HashMap[PrecKey, ArrayBuffer[GhostTransition]]
	val includes = new ArrayBuffer[GhostTarget]
	val includeGroups = new HashMap[PrecKey, ArrayBuffer[GhostTarget]]
	
	
	
	def +=(gt:GhostTransition) = {
		transitions += gt
		val key = PrecKey(gt.q1, gt.pepCompId)
		if (!transitionGroups.contains(key))
			transitionGroups(key) = new ArrayBuffer
		transitionGroups(key) += gt
	}
	
	
	
	def add(key:PrecKey, gt:GhostTarget) = {
		includes += gt
		if (!includeGroups.contains(key))
			includeGroups(key) = new ArrayBuffer
		includeGroups(key) += gt
	}
	
	
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
		
		if (includes.nonEmpty) {
			if (traml.targetList.isEmpty)
				traml.targetList = Some(new TargetList)
			val tl = traml.targetList.get
			tl.targetIncludes.clear
			for (t <- includes)
				tl.targetIncludes += t.toTarget
		}
		
		
			
		traml.write(w)
	}
}