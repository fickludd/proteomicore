package se.lth.immun.collection.numpress

import collection.mutable.Builder
import collection.mutable.Queue

import ms.numpress.MSNumpress
import collection.mutable.ArrayBuffer

object NumpressArray {
	abstract class NumpressIterator(bytes:ArrayBuffer[Byte], readAHead:Int = 10) extends Iterator[Double] {
		var byteIndex = 0
		val queue = new Queue[Double]
		def next:Double = {
			if (queue.isEmpty) 
				decompress
			queue.dequeue
		}
		def hasNext = queue.nonEmpty || byteIndex < bytes.length	
		protected def decompress:Unit
	}
}

abstract class NumpressArray(
		fixedPoint:Double = 0.0,
		readAHead:Int = 10
) extends Iterable[Double] with Builder[Double, Iterable[Double]] {

	//import MSNumpress._
	import ByteArray.Chunk
	import NumpressUtil._
	
	protected val ba = new ArrayBuffer[Byte]//ByteArray(chunkSize)
	var initiated = false
	val queue = new Queue[Double]
	var _fixedPoint:Double = Double.NaN
	
	protected def getIterator:Iterator[Double]
	protected def optimalFixedPoint(xs:Seq[Double]):Double
	protected def maxByteSize(nDoubles:Int):Int
	protected def compress(d:Double):Unit
	protected def prepareRead:Unit = {}
	
	private var _length = 0
	def length = _length
	def iterator = {
		if (!initiated) initiate(fixedPoint)
		processQueue
		prepareRead
		getIterator
	}
	
	def bytes:ArrayBuffer[Byte] = {
		if (!initiated) initiate(fixedPoint)
		processQueue
		prepareRead
		ba
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
		ba.clear 
	}
	def result:Iterable[Double] = this
	
	def initiate(fp:Double = 0.0) = {
		val _fp = 
			if (fp > 0) fp 
			else optimalFixedPoint(queue)
		encodeFixedPoint(_fp, ba)
		_fixedPoint = _fp
		initiated = true
	}
	
	def processQueue = {
		for (d <- queue) 
			compress(d)
		queue.clear
	}
}