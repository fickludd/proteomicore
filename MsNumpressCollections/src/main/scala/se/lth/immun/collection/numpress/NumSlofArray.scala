package se.lth.immun.collection.numpress

import collection.mutable.Builder
import collection.mutable.Queue

import ms.numpress.MSNumpress

object NumSlofArray {
	import MSNumpress._
	import NumpressArray._
	
	class NumSlofIterator(
			ba:ByteArray, 
			readAHead:Int = 10
	) extends NumpressIterator(ba, readAHead) {
		val fixedPoint = decodeFixedPoint(ba.chunks.head.a)
		byteIndex += 8
		def hasNext:Boolean = 
			queue.nonEmpty || hasBytes(2)
		
		def decompress = {
			for (i <- 0 until readAHead) {
				if (hasBytes(2)) {
					val x:Int = (0xff & currByteAndMove) | ((0xff & currByteAndMove) << 8)
					queue += math.exp((0xffff & x).toDouble / fixedPoint) - 1
				}
			}
		}
	}
	
	def optimalFixedPoint(maxVal:Double) =
		math.floor(0xFFFF / math.log(maxVal + 1))
}

class NumSlofArray(
		fixedPoint:Double = 0.0,
		readAHead:Int = 10,
		chunkSize:Int = 1024
) extends NumpressArray(fixedPoint, readAHead, chunkSize) {

	import ByteArray.Chunk
	import NumSlofArray._
	import MSNumpress._
	
	def getIterator = new NumSlofIterator(ba, readAHead)
	def optimalFixedPoint(xs:Seq[Double]) = optimalSlofFixedPoint(xs.toArray, xs.size)
	def maxByteSize(nDoubles:Int) = nDoubles * 2
	def compress(d:Double, c:Chunk):Int = {
		val x = (math.log(d+1) * fixedPoint + 0.5).toInt
		c.a(c.i) 	= (0xff & x).toByte
		c.a(c.i+1) 	= (x >> 8).toByte
		2
	}
	
	override def sizeHint(s:Int) = ba.byteSizeHint(8 + s*2)
}