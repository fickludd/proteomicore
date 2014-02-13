package se.lth.immun.mzml.ghost

import se.lth.immun.mzml._
import se.lth.immun.base64.Base64
import java.util.zip.Inflater
import java.util.zip.Deflater
import java.util.Arrays
import java.nio.ByteBuffer
import Ghost._
import ms.numpress.MSNumpress
import ms.numpress.MSNumpress._

object GhostBinaryDataArray {

	case class BytesLength(bytes:Array[Byte], length:Int)
	
	trait DataType
	case class Unknown extends DataType
	case class Time extends DataType
	case class Intensity extends DataType
	case class MZ extends DataType
	
	case class DataDef(
			val doublePrecision:Boolean,
			val zlibCompression:Boolean,
			val numCompression:String,
			val dataType:DataType,
			val externalBinary:Boolean) {
		
		def asType(dt:DataType) =
			new DataDef(doublePrecision, zlibCompression, numCompression, dt, externalBinary)
	}
	
	def read(
			b:BinaryDataArray, 
			defaultArrayLength:Int
	):(DataDef, Array[Double]) = {
		var encLength 		= b.encodedLength
		var dataLength 		= 
			if (b.arrayLength.isDefined) 	b.arrayLength.get 
			else if (b.extLength > 0) 		b.extLength
			else 							defaultArrayLength
		var double			= true
		var zlibCompression	= false
		var numCompression	= "NO"
		var dataType:DataType = Unknown()
		
		b.cvParams.foreach(cv =>
			cv.accession match {
				case BIT_32_ACC 			=> double = false
				case BIT_64_ACC 			=> double = true
				case NO_COMPRESSION_ACC 	=> zlibCompression = false
				case ZLIB_COMPRESSION_ACC 	=> zlibCompression = true
				case ACC_NUMPRESS_LINEAR	=> numCompression = ACC_NUMPRESS_LINEAR
				case ACC_NUMPRESS_PIC		=> numCompression = ACC_NUMPRESS_PIC
				case ACC_NUMPRESS_SLOF		=> numCompression = ACC_NUMPRESS_SLOF
				case TIME_ARRAY_ACC 		=> dataType = Time()
				case INTENSITY_ARRAY_ACC 	=> dataType = Intensity()
				case MZ_ARRAY_ACC 			=> dataType = MZ()
				case _ => {}
			})
		
		if (encLength == 0 && b.extLength == 0 || dataType == Unknown())
			return (DataDef(double, zlibCompression, numCompression, Unknown(), false), Array())
		
		// if extern take bytes else Base64-parse bytes
		// if zlibCompress, decompress
		// if numCompress, decompress
		// parse 32bit or 64bit
		// put in time or intensity array
				
		val rawBytes = 
			if (b.extLength == 0) 	Base64.decoder.decode(b.binary)
			else					b.extBinary.array
			
		val afterZlib = 
			if (zlibCompression) {
				val decompressor 	= new Inflater
				decompressor.setInput(rawBytes)
				val decompressed 	= new Array[Byte](dataLength * Base64.DOUBLE_SIZE)
				var readDataLength 	= decompressor.inflate(decompressed)
				decompressor.end
				BytesLength(decompressed, readDataLength)
			} else
				BytesLength(rawBytes, rawBytes.length)
		
		val afterNumpress =
			if (numCompression == "NO") {
				if (double) 	parseDoubleArray(afterZlib.bytes, afterZlib.length)
				else 			parseFloatArray(afterZlib.bytes, afterZlib.length)
			} else
				MSNumpress.decode(numCompression, afterZlib.bytes, afterZlib.length).take(dataLength)
		
		if (afterNumpress.length != dataLength)
			throw new GhostException("Missing doubles in decompressed data, got "
								+ afterNumpress.length + "/" + dataLength + " doubles")
		
		return (DataDef(double, zlibCompression, numCompression, 
						dataType, b.extLength > 0), 
				afterNumpress)
	}
	
	
	def parseDoubleArray(arr:Array[Byte], nBytes:Int):Array[Double] = {
		val rest = nBytes % Base64.DOUBLE_SIZE
		val retSize = nBytes / Base64.DOUBLE_SIZE
    	val ret = new Array[Double](retSize + {if (rest != 0) 1 else 0})
    	for (d <- 0 until retSize)
    		ret(d) = Base64.parseDouble(arr, d * Base64.DOUBLE_SIZE)
    	
    	if (rest != 0) {
    		ret(retSize) = 0
    		for (b <- 0 until rest)
    			ret(retSize) += arr(retSize * Base64.DOUBLE_SIZE + b) << b * Base64.BYTE_SIZE
    	}
    	return ret
	}
	
	def parseFloatArray(arr:Array[Byte], nBytes:Int):Array[Double] = {
		val rest = nBytes % Base64.FLOAT_SIZE
		val retSize = nBytes / Base64.FLOAT_SIZE
    	val ret = new Array[Double](retSize + {if (rest != 0) 1 else 0})
    	for (f <- 0 until retSize)
    		ret(f) = Base64.parseFloat(arr, f * Base64.FLOAT_SIZE)
    	
    	if (rest != 0) {
    		ret(retSize) = 0
    		for (b <- 0 until rest)
    			ret(retSize) += arr(retSize * Base64.FLOAT_SIZE + b) << b * Base64.BYTE_SIZE
    	}
    	return ret
	}
	
		
	
	
	def toBinaryDataArray(a:Array[Double], dataDef:DataDef):BinaryDataArray = {
		var double			= true
		var compression		= false
		
		var b = new BinaryDataArray
		dataDef.dataType match {
			case Time() =>
				b.cvParams += new CvParam {
					cvRef = "MS"
					accession = TIME_ARRAY_ACC
					name = "time array"
					unitCvRef = Some("UO")
					unitAccession = Some("UO:0000010")
					unitName = Some("second")
				}
			case Intensity() =>
				b.cvParams += new CvParam {
					cvRef = "MS"
					accession = INTENSITY_ARRAY_ACC
					name = "intensity array"
					unitCvRef = Some("MS")
					unitAccession = Some("MS:1000131")
					unitName = Some("number of counts")
				}
			case MZ() =>
				b.cvParams += new CvParam {
					cvRef = "MS"
					accession = INTENSITY_ARRAY_ACC
					name = "intensity array"
					unitCvRef = Some("MS")
					unitAccession = Some("MS:1000131")
					unitName = Some("number of counts")
				}
			case x =>
				throw new GhostException("Cannot write binary data of unknown type "+x)
		}
		
		if (dataDef.zlibCompression)
			b.cvParams += new CvParam {
				cvRef = "MS"
				accession = ZLIB_COMPRESSION_ACC
				name = "zlib compression"
			}
		else
			b.cvParams += new CvParam {
				cvRef = "MS"
				accession = NO_COMPRESSION_ACC
				name = "no compression"
			}
		
		if (dataDef.doublePrecision)
			b.cvParams += new CvParam {
				cvRef = "MS"
				accession = BIT_64_ACC
				name = "64-bit float"
			}
		else
			b.cvParams += new CvParam {
				cvRef = "MS"
				accession = BIT_32_ACC
				name = "32-bit float"
			}
		
		val afterNumpress = dataDef.numCompression match {
			case ACC_NUMPRESS_LINEAR => 
				b.cvParams += new CvParam {
					cvRef = "MS"
					accession = ACC_NUMPRESS_LINEAR
					name = "MS-Numpress linear prediction compression"
				}
				val fixedPoint = MSNumpress.optimalLinearFixedPoint(a, a.length)
				val result = new Array[Byte](a.length * 5 + 8)
				val encBytes = MSNumpress.encodeLinear(a, a.length, result, fixedPoint)
				BytesLength(result, encBytes)
				
			case ACC_NUMPRESS_PIC => 
				b.cvParams += new CvParam {
					cvRef = "MS"
					accession = ACC_NUMPRESS_PIC
					name = "MS-Numpress positive integer compression"
				}
				val result = new Array[Byte](a.length * 5)
				val encBytes = MSNumpress.encodePic(a, a.length, result)
				BytesLength(result, encBytes)
				
			case ACC_NUMPRESS_SLOF => 
				b.cvParams += new CvParam {
					cvRef = "MS"
					accession = ACC_NUMPRESS_SLOF
					name = "MS-Numpress short logged float compression"
				}
				val fixedPoint = MSNumpress.optimalSlofFixedPoint(a, a.length)
				val result = new Array[Byte](a.length * 2 + 8)
				val encBytes = MSNumpress.encodeSlof(a, a.length, result, fixedPoint)
				BytesLength(result, encBytes)
				
			case _ => 
				if (double)	BytesLength(codeDoubleArray(a), a.length * Base64.DOUBLE_SIZE)
				else 		BytesLength(codeFloatArray(a), a.length * Base64.FLOAT_SIZE)
		}
		
		
		val afterZlib = 
			if (dataDef.zlibCompression) {
				val compressor 		= new Deflater
				compressor.setInput(afterNumpress.bytes, 0, afterNumpress.length)
				val compressed 		= new Array[Byte](afterNumpress.length)
				var zippedLength 	= compressor.deflate(compressed)
				compressor.end
				BytesLength(compressed, zippedLength)
			} else afterNumpress
		
		
		if (dataDef.externalBinary) {
			b.extLength 		= a.length
			b.extBinary 		= ByteBuffer.wrap(afterZlib.bytes, 0, afterZlib.length)
			b.encodedLength 	= afterZlib.length
		} else {
			b.binary 		= Base64.decoder.encodeToString(Arrays.copyOfRange(afterZlib.bytes, 0, afterZlib.length))
			b.encodedLength = b.binary.length
		}
		return b
	}
	
	
	
	def codeDoubleArray(arr:Array[Double]):Array[Byte] = {
		var ba = new Array[Byte](arr.length * Base64.DOUBLE_SIZE)
		for (d <- 0 until arr.length)
			codeDouble(arr(d), ba, d * Base64.DOUBLE_SIZE)
		return ba
	}
	
	def codeFloatArray(arr:Array[Double]):Array[Byte] = {
		var ba = new Array[Byte](arr.length * Base64.FLOAT_SIZE)
		for (f <- 0 until arr.length)
			codeFloat(arr(f).toFloat, ba, f * Base64.FLOAT_SIZE)
		return ba
	}
	
	def codeFloat(f:Float, ba:Array[Byte], startByte:Int) = {
		var i = float2int(f)
		for (a <- startByte until startByte + Base64.FLOAT_SIZE)
			ba(a) = (i >> (a * Base64.BYTE_SIZE)).toByte
	}

	def codeDouble(d:Double, ba:Array[Byte], startByte:Int) = {
		var l = double2long(d)
		for (a <- startByte until startByte + Base64.DOUBLE_SIZE)
			ba(a) = (l >> (a * Base64.BYTE_SIZE)).toByte
	}
	
	def int2float = java.lang.Float.intBitsToFloat _
	def float2int = java.lang.Float.floatToIntBits _
	def long2double = java.lang.Double.longBitsToDouble _
	def double2long = java.lang.Double.doubleToLongBits _
}