package se.lth.immun.graphs.util

class Size(
		val width:Int,
		val height:Int
) {}

class Rect(
		val x0:Double,
		val y0:Double,
		val xn:Double,
		val yn:Double
) {
	def width = xn - x0
	def height = yn + y0
}

class IntRect(
		val x0:Int,
		val y0:Int,
		val xn:Int,
		val yn:Int
) {
	def width = xn - x0
	def height = yn + y0
}

class Interval[X](
		val x0:X,
		val xn:X
) {}