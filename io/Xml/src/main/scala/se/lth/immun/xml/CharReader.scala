package se.lth.immun.xml

import java.io.Reader

object CharReader {
	val BEFORE_READ = -2
	val EOF = -1
}

class CharReader(reader:Reader) {
	
	var char:Int = CharReader.BEFORE_READ
	var lineNo:Int = 0
	var colNo:Int = 0
	
	def next = {
		char = reader.read()
		char
	}
	
	def until(c:Char):String = {
		var sb = new StringBuilder
		
		if (char == CharReader.BEFORE_READ)
			char = reader.read()
		
		while (char != c) {
			if (char == CharReader.EOF) return sb.toString()
			val c = char.toChar
			if (c == '\n') {
				lineNo += 1
				colNo = 0
			} else
				colNo += 1
			
			sb += c
			next
		}
		
		sb.result
	}
	
	def xmlElement:String = {
		var sb = new StringBuilder
		
		if (char == CharReader.BEFORE_READ || char.toChar == '<')
			char = reader.read()
		
		while (char != '>') {
			if (char == CharReader.EOF) return sb.toString()
			val c = char.toChar
			c match {
				case '\\' => 
					sb += c
					sb += next.toChar
				case '"' =>
					sb ++= textElement
				case _ =>
					sb += c 
			}
			next
		}
		
		sb.result
	}
	
	private def textElement:String = {
		var sb = new StringBuilder
		
		sb += '"'
		next
		
		while (char != '"') {
			if (char == CharReader.EOF) return sb.toString()
			val c = char.toChar
			c match {
				case '\\' => 
					sb += c
					sb += next.toChar
				case _ =>
					sb += c
			}
			next
		}
		
		sb += '"'
		sb.result
	}

	def lineTag = "[line "+lineNo+"]"
}