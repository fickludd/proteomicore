package se.lth.immun.traml.ghost

import se.lth.immun.traml.Compound

object GhostCompound {
	
	def fromCompound(c:Compound):GhostCompound = {
		val gc = new GhostCompound
		gc.compound = c
		return gc
	}
}

class GhostCompound {

	var compound:Compound = null
	
	def toCompound = {
		compound
	}
}