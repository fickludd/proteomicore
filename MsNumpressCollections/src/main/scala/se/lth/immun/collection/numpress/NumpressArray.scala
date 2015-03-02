package se.lth.immun.collection.numpress

import collection.mutable.Builder
import collection.mutable.Queue

import ms.numpress.MSNumpress

object NumpressArray {
	abstract class NumpressIterator(arr:ByteArray, readAHead:Int = 10) extends Iterator[Double] {
		var chunkIndex = 0
		var byteIndex = 0
		val queue = new Queue[Double]
		def next:Double = {
			if (queue.isEmpty) 
				decompress
			queue.dequeue
		}
		protected def currByte = {
			//println(chunkIndex + " " + byteIndex)
			arr.chunks(chunkIndex).a(byteIndex)
		}
		protected def hasBytes(n:Int):Boolean = 
			chunkIndex < arr.chunks.length &&
			arr.chunks.drop(chunkIndex).map(_.i).sum - byteIndex >= n
		protected def currByteAndMove:Byte = {
			val b = currByte
			byteIndex += 1
			if (byteIndex == arr.chunks(chunkIndex).i) {
				chunkIndex += 1
				byteIndex = 0
			}
			b
		}
			
		protected def decompress:Unit
	}
}

abstract class NumpressArray(
		fixedPoint:Double = 0.0,
		readAHead:Int = 10,
		chunkSize:Int = 1024
) extends Iterable[Double] with Builder[Double, Iterable[Double]] {

	import MSNumpress._
	import ByteArray.Chunk
	
	val ba = new ByteArray(chunkSize)
	var initiated = false
	val queue = new Queue[Double]
	var _fixedPoint:Double = Double.NaN
	
	protected def getIterator:Iterator[Double]
	protected def optimalFixedPoint(xs:Seq[Double]):Double
	protected def maxByteSize(nDoubles:Int):Int
	protected def compress(d:Double, c:Chunk):Int
	
	private var _length = 0
	def length = _length
	def iterator = {
		if (!initiated) initiate(fixedPoint)
		if (queue.nonEmpty) processQueue
		getIterator
	}
	
	def +=(d:Double) = {
		_length += 1
		queue += d
		if (!initiated) {
			if (queue.size > math.max(2, readAHead))
				initiate(fixedPoint)
		}
		if (queue.size > math.max(2, readAHead))
			processQueue
		this
	}
	def clear = {
		initiated = false
		queue.clear
		_fixedPoint = Double.NaN
		ba.clearBytes
	}
	def result:Iterable[Double] = this
	
	def initiate(fp:Double = 0.0) = {
		val _fp = 
			if (fp > 0) fp 
			else optimalFixedPoint(queue)
		encodeFixedPoint(_fp, ba.chunk.a)
		ba.chunk.i += 8
		_fixedPoint = _fp
		initiated = true
	}
	
	def processQueue = {
		ba.prepWrite(maxByteSize(queue.size))
		val c = ba.chunk
		for (d <- queue) 
			c.i += compress(d, c)
		queue.clear
	}
}