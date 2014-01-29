package se.lth.immun.math

import scala.util.Random

object Maths {
	
	private val log2C = math.log(2)
	def log2(x:Double):Double = math.log(x) / log2C
	
	
	
	def round(a:Double, dec:Int):Double = {
		var fac = math.pow(10, dec)
		math.round(a * fac) / fac
	}
}