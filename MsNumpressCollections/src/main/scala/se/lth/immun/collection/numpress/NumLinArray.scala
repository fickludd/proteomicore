package se.lth.immun.collection.numpress

import collection.mutable.Queue
import collection.mutable.ArrayBuffer

import ms.numpress.MSNumpress



object NumLinArray {
	
	import NumpressUtil._
	import NumpressArray._
	
	class NumLinIterator(
			arr:ArrayBuffer[Byte], 
			readAHead:Int = 10,
			nDoubles:Int
	) extends NumpressIterator(arr, readAHead) {
		val fixedPoint = decodeFixedPoint(arr)
		byteIndex += 8
		
		val ri = 2
		val ints = new Array[Long](3)
		var extrapol = 0L
		var y = 0L
		var nRead = 0
		var half = false
		
		if (byteIndex + 4 <= arr.length) {
			ints(1) = 0
			for (i <- 0 until 4)
				ints(1) = ints(1) | ( (0xFFl & arr(8+i)) << (i*8))
			queue += ints(1) / fixedPoint
			byteIndex += 4
			nRead += 1
		}
		if (byteIndex + 4 <= arr.length) {
			ints(2) = 0
			for (i <- 0 until 4)
				ints(2) = ints(2) | ( (0xFFl & arr(12+i)) << (i*8))
			queue += ints(2) / fixedPoint
			byteIndex += 4
			nRead += 1
		}
			
			
		override def hasNext:Boolean = 
			queue.nonEmpty || nRead < nDoubles
		
		def decompress = {
			for (i <- nRead until math.min(nDoubles, nRead + readAHead)) {
				ints(0) = ints(1)
				ints(1) = ints(2)
				ints(2) = decompressLong
				
				extrapol = ints(1) + (ints(1) - ints(0))
				y = extrapol + ints(2)
				queue += y / fixedPoint
				ints(2) = y
				nRead += 1
			}
		}
			
		def decompressLong:Long = {
			var head = 0
			var n = 0
			var res = 0L
			val mask:Long = 0xf0000000
			var hb = 0
			
			if (!half)
				head = (0xff & arr(byteIndex)) >> 4
			else {
				head = 0xf & arr(byteIndex)
				byteIndex += 1
			}
			half = !half
			
			if (head <= 8)
				n = head
			else { 
				// leading ones, fill in res
				n = head - 8
				for (i <- 0 until n)
					res = res | (mask >> (4*i))
			}
	
			if (n == 8) return 0
				
			for (i <- n until 8) {
				if (!half)
					hb = (0xff & arr(byteIndex)) >> 4
				else {
					hb = 0xf & arr(byteIndex)
					byteIndex += 1
				}
				res = res | (hb << ((i-n)*4))
				half = !half;
			}
			
			return res
		}
	}
	
	def optimalFixedPoint(data:Seq[Double]):Double = {
		if (data.isEmpty) return 0
		if (data.length == 1) return math.floor(0xFFFFFFFFl / data(0))
		var maxDouble = math.max(data(0), data(1))
		
		for (i <- 2 until data.length) {
			val extrapol 	= data(i-1) + (data(i-1) - data(i-2));
			val diff 		= data(i) - extrapol;
			maxDouble 		= math.max(maxDouble, math.ceil(math.abs(diff)+1))
		}

		return math.floor(0x7FFFFFFFl / maxDouble)
	}
	
	
}

class NumLinArray(
		fixedPoint:Double = 0.0,
		readAHead:Int = 10
) extends NumpressArray(fixedPoint, readAHead) {

	import NumLinArray._
	
	override def initiate(fp:Double = 0.0) = {
		super.initiate(fp)
		
		if (queue.nonEmpty) {
			ints(1) = (queue.dequeue * _fixedPoint + 0.5).toLong
			for (i <- 0 until 4) 
				ba += ((ints(1) >> (i*8)) & 0xff).toByte
		}
		if (queue.nonEmpty) {
			ints(2) = (queue.dequeue * _fixedPoint + 0.5).toLong
			for (i <- 0 until 4) 
				ba += ((ints(2) >> (i*8)) & 0xff).toByte
		}
	}
	
	def getIterator = new NumLinIterator(ba, readAHead, length)
	def optimalFixedPoint(xs:Seq[Double]) = MSNumpress.optimalLinearFixedPoint(xs.toArray, xs.size)
	def maxByteSize(nDoubles:Int) = nDoubles * 5
	
	
	var encodedFirst 	= 0
	val ints 			= new Array[Long](3)
	val halfBytes 		= new Array[Byte](10)
	var halfByteCount 	= 0
	def compress(d:Double):Unit = {
		ints(0) = ints(1)
		ints(1) = ints(2)
		ints(2) = (d * _fixedPoint + 0.5).toLong
		val extrapol = ints(1) + (ints(1) - ints(0))
		val diff = ints(2) - extrapol
		if (halfByteCount == 1)
			ba.reduceToSize(ba.length - 1)
		
		halfByteCount += NumpressUtil.encodeInt(diff, halfBytes, halfByteCount)
		
		for (hbi <- 1 until halfByteCount by 2) 
			ba += ((halfBytes(hbi-1) << 4) | (halfBytes(hbi) & 0xf)).toByte
		
		if (halfByteCount % 2 != 0) {
			halfBytes(0) = halfBytes(halfByteCount-1)
			halfByteCount = 1
			ba += (halfBytes(0) << 4).toByte
		} else 
			halfByteCount = 0
	}
	
	override def sizeHint(s:Int) = ba.sizeHint(16 + (s-2)*5)
}