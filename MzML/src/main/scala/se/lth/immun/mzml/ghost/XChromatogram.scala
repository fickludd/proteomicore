package se.lth.immun.mzml.ghost

class XChromatogram(
		val q1:Double,
		val q3:Double,
		val ce:Double,
		val intensities:Seq[Double] = List(),
		val times:Seq[Double] = List()
) {
	def length = times.length
	override def toString = "["+q1.toInt+", "+q3.toInt+", "+ce+"] "+times.length+" values"
}