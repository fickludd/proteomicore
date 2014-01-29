package se.lth.immun.graphs.util

import java.awt.Color

object ColorTransform {
	def relPos(x:Double, x0:Double, xn:Double):Double = 
		if (x0 == xn) xn else (x-x0) / (xn-x0)
	
	def blend(c1:Color, c2:Color, k:Double):Color =
		new Color(
			((1-k) * c1.getRed 		+ k * c2.getRed		).toInt,
			((1-k) * c1.getGreen 	+ k * c2.getGreen	).toInt,
			((1-k) * c1.getBlue		+ k * c2.getBlue	).toInt
		)
}




trait ColorTransform {
	/*
	 * Transforms 0.0 <= d <= 1.0 into a color
	 */
	def getColor(d:Double):Color
}




class ThreePointGradientCT(
		var minColor:Color,
		var midColor:Color,
		var maxColor:Color
) extends ColorTransform {
	import ColorTransform._
	
	// UNIT TEST THIS !!
	/*
	 * Transforms 0.0 <= d <= 1.0 into a color
	 */
	def getColor(d:Double):Color = 
		if (d < 0.0 || d > 1.0)
			throw new Exception("d("+d+") not 0.0 <= d <= 1.0")
		else if (d < 0.5)
			return blend(minColor, midColor, d*2)
		else 
			return blend(midColor, maxColor, (d-0.5)*2)
}




class TwoPointGradientCT(
		var minColor:Color,
		var maxColor:Color
) extends ColorTransform {
	import ColorTransform._
	
	// UNIT TEST THIS !!
	/*
	 * Transforms 0.0 <= d <= 1.0 into a color
	 */
	def getColor(d:Double):Color = 
		if (d < 0.0 || d > 1.0)
			throw new Exception("d("+d+") not 0.0 <= d <= 1.0")
		else 
			return blend(minColor, maxColor, d)
}