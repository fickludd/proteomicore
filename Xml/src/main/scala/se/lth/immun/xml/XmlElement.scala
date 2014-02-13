package se.lth.immun.xml

import scala.collection.mutable.HashMap

object XmlElement {
	val NULL = new XmlElement("EOF/")
}
class XmlElement(str:String) {
	
	var start = false
	var end = false
	var leaf = true
	
	if (str.charAt(0) == '/') {
		start = false
		end = true
	} else {
		start = true
		end = str.charAt(str.length()-1) == '/'
	}
	
	var elementStr = XmlUtil.strip(str, Array('<', '>', '/', ' ', '\t', '\n'))
	
	
	var t = elementStr span (not(' '))
	var name = t._1
	var attributes = new HashMap[String, String]()
	
	var rest = readAttribute(t._2, attributes)
	while (rest.length != 0)
		rest = readAttribute(rest, attributes)

	
	private def not(c:Char)(x:Char) = c != x
	private def rest(str:String) = str.length > 0
	
	private def readAttribute(str:String, map:HashMap[String, String]):String = {
		var stripped = XmlUtil.strip(str)
		stripped.length match {
			case 0 => return ""
			case _ => {
				var t = stripped span (not('='))
				if (!rest(t._2)) return ""
				var t2 = t._2.span((not('"')))._2.drop(1).span(not('"'))
				map += t._1 -> t2._1
				return t2._2.drop(1)
			}
		}
	}
}
