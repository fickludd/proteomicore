package se.lth.immun.files

import java.io.BufferedReader;

object Delimited {
		
	def readRow(delimiter:Char, immunity:Char, reader:BufferedReader):List[String] = {
		return readRow(delimiter, immunity, reader.readLine())
	}
	
	def readRow(delimiter:Char, immunity:Char, line:String):List[String] = {
		var words = List[String]()
		
		if (line == null || line == "") return words
		var wordStart = 0
		var isImmune = false
		var stripChars = Array(' ', '\t', '\n', '\r', immunity)
		
		for (i <- 0 until line.length) {
			var c = line.charAt(i)
			if (c == delimiter && !isImmune) {
				words = words ::: (IO.strip(line.substring(wordStart, i), stripChars)) :: Nil
				wordStart = i + 1
			}
			else if (c == immunity)
				isImmune = !isImmune
		}
		words = words ::: (IO.strip(line.substring(wordStart, line.length), stripChars)) :: Nil
		
		return words
	}
}




class DelimitedReader(
		val delimiter:Char,
		val immunity:Char,
		reader:BufferedReader,
		readTags:Boolean = false) {
	
	val tags = if (readTags) readRow else null
	
	def readRow = Delimited.readRow(delimiter, immunity, reader)
}




class SparseDelimitedReader(
		val delimiter:Char,
		val immunity:Char,
		reader:BufferedReader) {
	
	val tags = _readRow
	
	var last = new Array[String](tags.length)
	
	def readRow():List[String] = {
		var row = _readRow.toArray
		for (i <- 0 until row.length) {
			if (row(i).length == 0)	row(i) = last(i)
			else 					last(i) = row(i)
		}
		return row.toList
	}
	
	def _readRow = Delimited.readRow(delimiter, immunity, reader)
}
