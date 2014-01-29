package se.lth.immun.xml

import java.io.OutputStream

class CountingOutputStream(val target:OutputStream) extends OutputStream {
	
	private var c:Long = 0
	def count = c
	
	override def write(b:Array[Byte]):Unit = {
		target.write(b)
		c += b.length
	}
	
	override def write(b:Int):Unit = {
		target.write(b)
		c += 1
	}
	
	override def write(b:Array[Byte], off:Int, len:Int):Unit = {
		target.write(b, off, len)
		c += len
	}
	
	override def flush = target.flush
	override def close = target.close
}