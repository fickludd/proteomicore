package se.lth.immun.xml

object XmlUtil {
	val NAME = "name"
    val VALUE = "value"
    val PARAM = "parameter"
	val COUNT = "count"
	val ABS_PATH = "absolute_path"
	val FILE_ID = "file_id"
		
		
	def strip(
				str:String, 
				chars:Array[Char] = Array(' ', '\t', '\n', '\r')
			):String = {
		if (str == null || str == "") return ""
		
		var start = 0
		var end = str.length
		var found = false
		while (!found)
			if (start >= end) return ""
			else if (!chars.contains(str.charAt(start))) found = true
			else start += 1
		
		found = false
		while (!found)
			if (start >= end) return ""
			else if (!chars.contains(str.charAt(end - 1))) found = true
			else end -= 1
		
		return str.substring(start, end)
	}
}