package se.lth.immun.graphs

import se.lth.immun.graphs.util.Axis
import se.lth.immun.graphs.util.Size
import se.lth.immun.graphs.util.IntRect

class GraphConverter {
	var size:Size = null
	var inner:IntRect = null
	
	def gx2px(gx:Double):Int = (gx*inner.width).toInt + inner.x0
	def px2gx(px:Int):Double = (px - inner.x0).toDouble / inner.width
	
	def gy2py(gy:Double):Int = ((1 - gy)*inner.height).toInt + inner.y0
	def py2gy(py:Int):Double = 1 - (py - inner.y0).toDouble / inner.height
}

trait XConverter[X] extends GraphConverter {
	var xAxis:Axis[X] = null
	
	def x2px(x:X):Int = gx2px(xAxis.x2gx(x))
	def px2x(px:Int):X = xAxis.gx2x(px2gx(px))
}


trait YConverter[Y] extends GraphConverter {
	var yAxis:Axis[Y] = null
	
	def y2py(y:Y):Int = gy2py(yAxis.x2gx(y))
	def py2y(py:Int):Y = yAxis.gx2x(py2gy(py))
}