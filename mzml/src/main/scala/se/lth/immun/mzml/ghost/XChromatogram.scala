package se.lth.immun.mzml.ghost

class XChromatogram(
		val q1:Double,
		val q3:Double,
		val ce:Double,
		val intensities:Array[Double] = Array(),
		val times:Array[Double] = Array()
) {
	def length = times.length
	override def toString = "["+q1.toInt+", "+q3.toInt+", "+ce+"] "+times.length+" values"
}