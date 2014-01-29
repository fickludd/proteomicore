package se.lth.immun.base64

object Base64 {
	val BYTE_SIZE = 8
	val BYTE_MASK = 0xFF
	val FLOAT_SIZE = 4
    val DOUBLE_SIZE = 8
    	
    val decoder = new org.apache.commons.codec.binary.Base64(-1, Array(), false)
	
	
	
	def int2float = java.lang.Float.intBitsToFloat _
	def float2int = java.lang.Float.floatToIntBits _
	def long2double = java.lang.Double.longBitsToDouble _
	def double2long = java.lang.Double.doubleToLongBits _

	
	
	def parseDouble(ba:Array[Byte], startByte:Int):Double = {
		var l:Long = 0
		for (a <- startByte until startByte + DOUBLE_SIZE)
			l = l | ((ba(a).toLong & BYTE_MASK) << (a * BYTE_SIZE))
		long2double(l)
	}
	
	def parseFloat(ba:Array[Byte], startByte:Int):Float = {
		var i:Int = 0
		for (a <- startByte until startByte + FLOAT_SIZE)
			i = i | ((ba(a).toInt & BYTE_MASK) << (a * BYTE_SIZE))
		int2float(i)
	}

	def codeFloat(f:Float, ba:Array[Byte], startByte:Int) = {
		var i = float2int(f)
		for (a <- startByte until startByte + FLOAT_SIZE)
			ba(a) = (i >> (a * BYTE_SIZE)).toByte
	}

	def codeDouble(d:Double, ba:Array[Byte], startByte:Int) = {
		var l = double2long(d)
		for (a <- startByte until startByte + DOUBLE_SIZE)
			ba(a) = (l >> (a * BYTE_SIZE)).toByte
	}
	
	
	
	def parseDoubleArray(obj:Any):Array[Double] = {
		obj match {
	    	case text:String => parseDoubleArray(decoder.decode(text))
	    	case arr:Array[Byte] => {
	    			var rest = arr.length % DOUBLE_SIZE
		    		//if (rest != 0) throw new IllegalArgumentException("ByteArray not multiple of " + bytes + " long.")
	    			var retSize = arr.length / DOUBLE_SIZE
			    	var ret = new Array[Double](retSize + {if (rest != 0) 1 else 0})
			    	for (d <- 0 until retSize)
			    		ret(d) = parseDouble(arr, d * DOUBLE_SIZE)
			    	
			    	if (rest != 0) {
			    		ret(retSize) = 0
			    		for (b <- 0 until rest)
			    			ret(retSize) += arr(retSize * DOUBLE_SIZE + b) << b*BYTE_SIZE
			    	}
			    	return ret
		    	}
	    	case _ => throw new IllegalArgumentException("Unparsable type.")
	    }
	}
	
	def parseFloatArray(obj:Any):Array[Double] = {
		obj match {
	    	case text:String => parseFloatArray(decoder.decode(text))
	    	case arr:Array[Byte] => {
	    			var rest = arr.length % FLOAT_SIZE
		    		//if (rest != 0) throw new IllegalArgumentException("ByteArray not multiple of " + bytes + " long.")
	    			var retSize = arr.length / FLOAT_SIZE
			    	var ret = new Array[Double](retSize + {if (rest != 0) 1 else 0})
			    	for (f <- 0 until retSize)
			    		ret(f) = parseFloat(arr, f * FLOAT_SIZE)
			    	
			    	if (rest != 0) {
			    		ret(retSize) = 0
			    		for (b <- 0 until rest)
			    			ret(retSize) += arr(retSize * FLOAT_SIZE + b) << b * BYTE_SIZE
			    	}
			    	return ret
		    	}
	    	case _ => throw new IllegalArgumentException("Unparsable type.")
	    }
	}
	
	
	
	def codeDoubleArray(arr:Array[Double]):String = {
		var ba = new Array[Byte](arr.length * DOUBLE_SIZE)
		for (d <- 0 until arr.length)
			codeDouble(arr(d), ba, d * DOUBLE_SIZE)
		return decoder.encodeToString(ba)
	}
	
	def codeFloatArray(arr:Array[Float]):String = {
		var ba = new Array[Byte](arr.length * FLOAT_SIZE)
		for (f <- 0 until arr.length)
			codeFloat(arr(f), ba, f * FLOAT_SIZE)
		return decoder.encodeToString(ba)
	}
}