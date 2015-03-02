package se.lth.immun.collection.numpress

object ByteArray {
	
	class Chunk(chunkSize:Int) {
		val a = new Array[Byte](chunkSize)
		var i = 0
		
		override def toString = "Chunk(n="+i+")"
	}
	
	class ByteArrayIterator(ba:ByteArray) extends Iterator[Byte] {
		var chunkIndex = 0
		var byteIndex = 0
		def hasNext = 
			chunkIndex < ba.chunks.length &&
			ba.chunks.drop(chunkIndex).map(_.i).sum > byteIndex
		def next = {
			val b = ba.chunks(chunkIndex).a(byteIndex)
			byteIndex += 1
			if (byteIndex == ba.chunks(chunkIndex).i) {
				chunkIndex += 1
				byteIndex = 0
			}
			b
		}
	}
}

class ByteArray(val chunkSize:Int = 2048) extends Iterable[Byte] {

	import ByteArray._
	
	var chunks: Seq[Chunk] = List(new Chunk(chunkSize))
	var capacity: Int = chunkSize
	
	def byteSize = chunks.map(_.i).sum
	
	def chunk:Chunk = chunks.last	
	
	private def byteResize(size: Int) {
		val nChunk = size / chunkSize
		if (chunks.length < nChunk) 
			chunks = chunks ++ (0 until (nChunk - chunks.length)).map(_ => new Chunk(chunkSize))
		
		capacity = chunks.length * chunkSize
	}
	
	def iterator = new ByteArrayIterator(this)
	
	override def foreach[U](f:Byte => U):Unit = 
		for (c <- chunks) c.a.take(c.i).foreach(f)
	
	def byteSizeHint(size: Int) = {
		if (capacity < size) byteResize(size)
	}
	
	def prepWrite(bytes:Int) = 
		if (chunkSize - chunks.last.i < bytes)
			chunks = chunks :+ new Chunk(chunkSize)
	
	
	def clearBytes = {
		capacity = 0
		chunks = Nil
	}
	
	override def toString = "ByteArray"
}