package se.lth.immun.collection.numpress

import collection.mutable.IndexedSeqLike
import collection.mutable.ResizableArray
import collection.generic.Growable

object NumpressUtil {

	def encodeFixedPoint(
			fixedPoint:Double, 
			result:Growable[Byte]
	):Unit = {
		val fp = java.lang.Double.doubleToLongBits(fixedPoint)
		result ++= (0 until 8).reverse.map(i => ((fp >> (8*i)) & 0xff).toByte) 
	}
	
	
	
	def decodeFixedPoint(
			bytes:Seq[Byte]
	):Double = {
		var fp = 0L
		for (i <- 0 until 8) 
			fp = fp | ((0xFFl & bytes(7-i)) << (8*i))
		
		java.lang.Double.longBitsToDouble(fp)
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
		var m:Long = 0
		val mask:Long = 0xf0000000
		val init:Long = x & mask
		
		def find_l_pos:Int = {
			for (i <- 1 until 8) {
				m = mask >> (4*i)
				if ((x & m) != 0)
					return i
			}
			return 8
		}
		
		def find_l_neg:Int = {
			for (i <- 1 until 8) {
				m = mask >> (4*i)
				if ((x & m) != m)
					return i
			}
			return 7
		}
		
		val l = (
				if (init == 0) 			find_l_pos // 1 <= l <= 8
				else if (init == mask) find_l_neg // 1 <= l <= 7
				else 					0
			)
		res(resOffset) = 
			if (init == mask) 	(8|l).toByte
			else 				l.toByte
		for (i <- l until 8) 
			res(resOffset+1+i-l) = (0xf & (x >> (4*(i-l)))).toByte
			
		return 1+8-l
	} 
}