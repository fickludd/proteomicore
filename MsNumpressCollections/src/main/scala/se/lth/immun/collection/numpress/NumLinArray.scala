package se.lth.immun.collection.numpress

import collection.mutable.Builder
import collection.mutable.Queue

import ms.numpress.MSNumpress
import ms.numpress.IntDecoder

object NumLinArray {
	
	import MSNumpress._
	import NumpressArray._
	
	class NumLinIterator(
			arr:NumLinArray, 
			readAHead:Int = 10
	) extends NumpressIterator(arr.ba, readAHead) {
		val fixedPoint = decodeFixedPoint(arr.ba.chunks.head.a)
		byteIndex += 8
		
		val ri = 2
		val ints = new Array[Long](3)
		var extrapol = 0L
		var y = 0L
		var nRead = 0
		var half = false
		
		val headChunk = arr.ba.chunks.head
		if (hasBytes(4)) {
			ints(1) = 0
			for (i <- 0 until 4)
				ints(1) = ints(1) | ( (0xFFl & headChunk.a(8+i)) << (i*8))
			queue += ints(1) / fixedPoint
			byteIndex += 4
			nRead += 1
		}
		if (hasBytes(4)) {
			ints(2) = 0
			for (i <- 0 until 4)
				ints(2) = ints(2) | ( (0xFFl & headChunk.a(12+i)) << (i*8))
			queue += ints(2) / fixedPoint
			byteIndex += 4
			nRead += 1
		}
			
			
		def hasNext:Boolean = 
			queue.nonEmpty || nRead < arr.length
		
		def decompress = {
			for (i <- nRead until math.min(arr.length, nRead + readAHead)) {
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
				head = (0xff & currByte) >> 4
			else
				head = 0xf & currByteAndMove;
	
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
					hb = (0xff & currByte) >> 4
				else
					hb = 0xf & currByteAndMove
	
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
	
	
	/**
	 * This encoding works on a 4 byte integer, by truncating initial zeros or ones.
	 * If the initial (most significant) half byte is 0x0 or 0xf, the number of such 
	 * halfbytes starting from the most significant is stored in a halfbyte. This initial 
	 * count is then followed by the rest of the ints halfbytes, in little-endian order. 
	 * A count halfbyte c of
	 * 
	 * 		0 <= c <= 8 		is interpreted as an initial c 		0x0 halfbytes 
	 * 		9 <= c <= 15		is interpreted as an initial (c-8) 	0xf halfbytes
	 * 
	 * Ex:
	 * 	int		c		rest
	 * 	0 	=> 	0x8
	 * 	-1	=>	0xf		0xf
	 * 	23	=>	0x6 	0x7	0x1
	 * 
	 * 	@x			the int to be encoded
	 *	@res		the byte array were halfbytes are stored
	 *	@resOffset	position in res were halfbytes are written
	 *	@return		the number of resulting halfbytes
	 */
	def encodeInt(x:Long, res:Array[Byte], resOffset:Int):Int = {
		var i:Int = 0
		var l:Int = 0
		var m:Long = 0
		val mask:Long = 0xf0000000
		val init:Long = x & mask
		var go = true
		
		def find_l_pos:Int = {
			for (i <- 0 until 8) {
				m = mask >> (4*i)
				if ((x & m) != 0)
					return i
			}
			return 8
		}
		
		def find_l_neg:Int = {
			for (i <- 0 until 8) {
				m = mask >> (4*i)
				if ((x & m) != m)
					return i
			}
			return 7
		}
		
		if (init == 0) {
			l = find_l_pos
			res(resOffset) = l.toByte
			for (i <- l until 8) 
				res(resOffset+1+i-l) = (0xf & (x >> (4*(i-l)))).toByte
			
			return 1+8-l

		} else if (init == mask) {
			l = find_l_neg
			res(resOffset) = (l | 8).toByte
			for (i <- l until 8) 
				res(resOffset+1+i-l) = (0xf & (x >> (4*(i-l)))).toByte
			
			return 1+8-l

		} else {
			res(resOffset) = 0
			for (i <- 1 until 8) 
				res(resOffset+1+i) = (0xf & (x >> (4*i))).toByte
			
			return 9

		}
	} 
}

class NumLinArray(
		fixedPoint:Double = 0.0,
		readAHead:Int = 10,
		chunkSize:Int = 1024
) extends NumpressArray(fixedPoint, readAHead, chunkSize) {

	import ByteArray.Chunk
	import MSNumpress._
	import NumLinArray._
	
	var encodedFirst = 0
	val ints = new Array[Long](3)
	val halfBytes = new Array[Byte](10)
	var halfByteCount = 0
	
	override def initiate(fp:Double = 0.0) = {
		super.initiate(fp)
		ba.prepWrite(8)
		val c = ba.chunk
		
		if (queue.nonEmpty) {
			ints(1) = (queue.dequeue * _fixedPoint + 0.5).toLong
			for (i <- 0 until 4) 
				c.a(8+i) = ((ints(1) >> (i*8)) & 0xff).toByte
			c.i += 4
		}
		if (queue.nonEmpty) {
			ints(2) = (queue.dequeue * _fixedPoint + 0.5).toLong
			for (i <- 0 until 4) 
				c.a(12+i) = ((ints(2) >> (i*8)) & 0xff).toByte
			c.i += 4
		}
	}
	
	def getIterator = {
		ensureWritten
		new NumLinIterator(this, readAHead)
	}
	def optimalFixedPoint(xs:Seq[Double]) = optimalLinearFixedPoint(xs.toArray, xs.size)
	def maxByteSize(nDoubles:Int) = nDoubles * 5
	def compress(d:Double, c:Chunk):Int = {
		ints(0) = ints(1)
		ints(1) = ints(2)
		ints(2) = (d * _fixedPoint + 0.5).toLong
		val extrapol = ints(1) + (ints(1) - ints(0))
		val diff = ints(2) - extrapol
		halfByteCount += NumLinArray.encodeInt(diff, halfBytes, halfByteCount)
		
		var i = c.i
		for (hbi <- 1 until halfByteCount by 2) {
			c.a(i) = ((halfBytes(hbi-1) << 4) | (halfBytes(hbi) & 0xf)).toByte
			i += 1
		}
		
		if (halfByteCount % 2 != 0) {
			halfBytes(0) = halfBytes(halfByteCount-1)
			halfByteCount = 1
		} else 
			halfByteCount = 0
		i - c.i
	}
	
	def ensureWritten = 
		if (halfByteCount == 1) {
			ba.prepWrite(1)
			ba.chunk.a(ba.chunk.i) = (halfBytes(0) << 4).toByte
			ba.chunk.i += 1
		}
}