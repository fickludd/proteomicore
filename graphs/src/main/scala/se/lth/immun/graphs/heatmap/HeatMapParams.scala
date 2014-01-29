package se.lth.immun.graphs.heatmap

import java.awt.Color
import se.lth.immun.graphs.util.ColorTransform
import se.lth.immun.graphs.util.ThreePointGradientCT



object HeatMapParams {
	abstract class ColoringScheme
	case class Row extends ColoringScheme
	case class Global extends ColoringScheme
	case class Fixed(min:Double, mid:Double, max:Double) extends ColoringScheme
}

class HeatMapParams[D](
		var toDouble:(D => Double),
		var filter:(D => Boolean),
		var transform:(Double => Double),
		var special:(D => Color)
) {
	/* size settings */
	var tileWidth = 4
	var tileHeight = 4
	var sep = 4
	
	/* colors */
	var colorTransform:ColorTransform = new ThreePointGradientCT(
		Color.RED,
		Color.YELLOW,
		Color.GREEN
	)
	
	var hmBGColor = new Color(0x222244)
	var headerBGColor = Color.LIGHT_GRAY
	var highlightColor = new Color(0xFFFFFF)
	var selectionColor = new Color(0xAAAAFF)
	
	/* misc */
	import HeatMapParams._
	var coloringScheme:ColoringScheme = Global()
	var dataMin:Double = Double.NaN
	var dataMid:Double = Double.NaN
	var dataMax:Double = Double.NaN
}