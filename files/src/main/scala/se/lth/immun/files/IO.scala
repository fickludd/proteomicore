package se.lth.immun.files


import java.io.File
import java.io.FileReader
import java.io.IOException

object IO {
	
	
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
	
	
	
	def resolvePath(path:String):Array[File] = {
		var file = new File(path)
		
        if (file.exists)
        {
            if (file.isFile) return Array(file)
            else {
            	return file.listFiles()
            }
        } else {
        	if (path.contains('*')) {
        		throw new IOException("Globbing not supported yet...")
        	} else
        		throw new IOException("Couldn't find files: " + path)
        }
	}
}
