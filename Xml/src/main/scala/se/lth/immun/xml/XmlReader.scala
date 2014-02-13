package se.lth.immun.xml

import java.io.Reader
import java.io.IOException
import scala.collection.mutable.HashMap
import scala.collection.mutable.Stack
import scala.collection.mutable.Queue

class XmlReader(
		xml:Reader, 
		var _saveToBuff:Boolean = false
) {
	
	private var elementString:String = ""
	private var cr = new CharReader(xml)
	private var _text = ""
	private var _rawText = ""
	private var buff		= new StringBuilder

	var stack:Stack[XmlElement] = new Stack
	var queue:Queue[XmlElement] = new Queue
	var nextElement:XmlElement 	= parseElement
	var text 	= ""
	var force 	= false
	
		
	def getBuff = buff.toString
	def getAndClearBuff = { val str = buff.toString; buff.clear; str }
	def saveToBuff_=(bool:Boolean) = _saveToBuff = bool
	def saveToBuff = _saveToBuff
	
	
	def top:XmlElement = stack.top
	private def push(e:XmlElement) = { 
		if (!stack.isEmpty) top.leaf = false
		stack.push(e) 
	}
	
	private def parseElement:XmlElement = {
		
		_text = cr.until('<')
		cr.next
		elementString = cr.until('>')
		cr.next
		if (_saveToBuff) buff ++= _rawText + _text
		_rawText = '<' + elementString + '>'

		if (elementString == "") {
			//if (_saveToBuff) buff ++= _text
			return XmlElement.NULL
		}
		if (elementString.startsWith("!--")) return parseElement
		return new XmlElement(elementString)
		
	}
	
	def next:String = {
		if (!stack.isEmpty && top == XmlElement.NULL) return nextElement.name
		while (!queue.isEmpty && !stack.isEmpty &&
				queue.front.name == stack.top.name
		) {
			queue.dequeue
			stack.pop
		}
		push(nextElement)
		if (nextElement.end) queue.enqueue(nextElement)
		
		var e = parseElement
		if (!nextElement.end && !e.start && e.end && e.name == nextElement.name)
			text = _text
		else
			text = ""
		while (!e.start) {
			queue.enqueue(e)
			e = parseElement
		}
		nextElement = e
		return e.name
	}
	
	def nextIn(e:XmlElement):Boolean = {
		var i = stack.indexOf(e)
		i >= queue.length
	}
	
	def in(e:XmlElement):Boolean = 
		stack.contains(e)
	
	def is(elementName:String):Boolean = {
		if (nextElement.name == elementName) {
			next
			return true
		}
		return false
	}
	
	def was(elementName:String):Boolean = 
		return top.name == elementName
	
	def until(elementName:String):XmlElement = {
		while (nextElement.name != elementName) {
			if (atEOF) return XmlElement.NULL
			next
		}
		next
		return top
	}
	
	def untilOneOf(elementNames:Seq[String]):XmlElement = {
		while (!elementNames.contains(nextElement.name)) {
			if (atEOF) return XmlElement.NULL
			next
		}
		next
		return top
	}
	
	def ensure(elementName:String) = {
		if (!is(elementName))
			throw new IOException(cr.lineTag+" Corrupt xml file: expected '"+elementName+"' tag, got '"+nextElement.name+"'")
	}
	
	def skipThis = {
		var e = top
		while (nextIn(e)) next
	}
	
	def skip(elementName:String) = {
		if (is(elementName)) skipThis
	}
	
	def skipAll(elementName:String) = {
		while (is(elementName)) skipThis
	}
	
	def exit(e:XmlElement) = {
		while (nextIn(e)) next
	}
	
	def atEOF:Boolean = nextElement == XmlElement.NULL
	
	
	
	def hasAttribute(attributeName:String):Boolean = top.attributes.contains(attributeName)
    def readAttributeString = readAttribute _
    def readAttribute(attributeName:String):String = 
    	top.attributes.get(attributeName) match {
		case Some(s) => s
		case None =>
			if (force) null
			else
				throw new IOException(cr.lineTag+" attribute '"+attributeName+"' missing from tag.")
		}
	
	
    def readAttributeInt(attributeName:String):Int = 
    	top.attributes.get(attributeName) match {
		case Some(s) => 
			try {
				s.toInt
			} catch {
				case _:Throwable => throw new IOException(cr.lineTag+" attribute '"+attributeName+"' with value '"+s+"' not parsable as Int.")
			}
		case None =>
			if (force) 0
			else
				throw new IOException(cr.lineTag+" attribute '"+attributeName+"' missing from tag.")
		}
	
    def readAttributeLong(attributeName:String):Long = 
    	top.attributes.get(attributeName) match {
		case Some(s) => 
			try {
				s.toLong
			} catch {
				case _:Throwable => throw new IOException(cr.lineTag+" attribute '"+attributeName+"' with value '"+s+"' not parsable as Long.")
			}
		case None =>
			if (force) 0L
			else
				throw new IOException(cr.lineTag+" attribute '"+attributeName+"' missing from tag.")
		}
	
    def readAttributeDouble(attributeName:String):Double = 
    	top.attributes.get(attributeName) match {
		case Some(s) => 
			try {
				s.toDouble
			} catch {
				case _:Throwable => throw new IOException(cr.lineTag+" attribute '"+attributeName+"' with value '"+s+"' not parsable as Double.")
			}
		case None =>
			if (force) Double.NaN
			else
				throw new IOException(cr.lineTag+" attribute '"+attributeName+"' missing from tag.")
		}
	
    
    
    def readOptional(attributeName:String):Option[String] = 
    	if (top.attributes.contains(attributeName)) 	Some(top.attributes(attributeName))
    	else											None
    def readOptionalString(attributeName:String):Option[String] = 
    	if (top.attributes.contains(attributeName)) 	Some(top.attributes(attributeName))
    	else											None
    def readOptionalInt(attributeName:String):Option[Int] = 
    	if (top.attributes.contains(attributeName)) 	Some(top.attributes(attributeName).toInt)
    	else											None
    def readOptionalLong(attributeName:String):Option[Long] = 
    	if (top.attributes.contains(attributeName)) 	Some(top.attributes(attributeName).toLong)
    	else											None
    def readOptionalDouble(attributeName:String):Option[Double] = 
    	if (top.attributes.contains(attributeName)) 	Some(top.attributes(attributeName).toDouble)
    	else											None
    def close = xml.close
}