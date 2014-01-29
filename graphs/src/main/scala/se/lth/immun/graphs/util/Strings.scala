package se.lth.immun.graphs.util

import java.awt.FontMetrics

object Strings {
	def width(str:String, fm:FontMetrics) = {
		var chars = str.toCharArray	
		fm.charsWidth(chars, 0, chars.length)
	}
}