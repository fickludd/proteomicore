package se.lth.immun.math

object Matrix {
	
	def get2d[T : Manifest](n:Int):Array[Array[T]] = get2d[T](n, n)
	def get2d[T : Manifest](n:Int, m:Int):Array[Array[T]] = {
		if (n <= 0 || m <= 0)
			throw new IllegalArgumentException("Matrix dimensions ["+n+"x"+m+"] must exceed 0")
		var arr = new Array[Array[T]](n)
		return arr.map(x => new Array[T](m))
	}
}