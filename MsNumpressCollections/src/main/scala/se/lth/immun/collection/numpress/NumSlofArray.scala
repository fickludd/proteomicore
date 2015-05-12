package se.lth.immun.collection.numpress

import collection.mutable.Queue
import collection.mutable.ArrayBuffer

import ms.numpress.MSNumpress

object NumSlofArray {
	import NumpressUtil._
	import NumpressArray._
	
	class NumSlofIterator(
			arr:ArrayBuffer[Byte], 
			readAHead:Int = 10
	) extends NumpressIterator(arr, readAHead) {
		val fixedPoint:Double = decodeFixedPoint(arr)
		byteIndex += 8
		
		def decompress = {
			for (i <- 0 until readAHead) {
				if (byteIndex+2 <= arr.length) {
					val x:Int = (0xff & arr(byteIndex)) | ((0xff & arr(byteIndex+1)) << 8)
					queue += math.exp((0xffff & x).toDouble / fixedPoint) - 1
					byteIndex += 2
				}
			}
		}
	}
	
	def optimalFixedPoint(maxVal:Double) =
		math.floor(0xFFFF / math.log(maxVal + 1))
}

class NumSlofArray(
		fixedPoint:Double = 0.0,
		readAHead:Int = 10
) extends NumpressArray(fixedPoint, readAHead) {

	import NumSlofArray._
	
	def getIterator = new NumSlofIterator(ba, readAHead)
	def optimalFixedPoint(xs:Seq[Double]) = MSNumpress.optimalSlofFixedPoint(xs.toArray, xs.size)
	def maxByteSize(nDoubles:Int) = nDoubles * 2
	def compress(d:Double):Unit = {
		val x = (math.log(d+1) * fixedPoint + 0.5).toInt
		ba += (0xff & x).toByte
		ba += (x >> 8).toByte
	}
	
	override def sizeHint(s:Int) = ba.sizeHint(8 + s*2)
}