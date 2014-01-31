package se.lth.immun.files

import java.io.File
import java.io.FilenameFilter

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

//import org.apache.commons.lang3.StringUtils

class RegExpFilenameFilter(
			val files:Boolean,
			val path:String
		) extends FilenameFilter {
	
	val ValidPath = new Regex((path map (c => c match {
									case '*' => ".*"
									case _ => c.toString
							})).reduceLeft(_+_))
	
	
	override def accept(dir:File, name:String):Boolean = 
		ValidPath.findPrefixOf(name) match {
			case None => false
			case Some(m) => 
					m.length == name.length && 
					new File(dir, name).isFile() == files
		}
	
}


object Glob {
	
	def resolveGlob(path:File):Array[File] = {
		if (path.isDirectory())
			return path.listFiles()
		var absPath = path.getAbsoluteFile
		//System.out.println("##")
		//System.out.println("Resolving GLOB: "+path.toString)
		var stableDir = getStablePrefix(absPath.getParentFile)
		var glob = absPath.toString.substring(stableDir.toString.length+1).replace("\\", "/").split('/').toList.filter(!_.isEmpty)
		return resolve(stableDir, glob).toArray
	}
	
	def resolve(base:File, glob:List[String]):Seq[File] = {
		//System.out.println("   "+base.toString+": "+glob.toString)
		glob match {
		case Nil 		=> Nil
		case fileName::Nil 	=> {
				var filter = new RegExpFilenameFilter(true, fileName)
				var files = base.listFiles(filter)
				return files.toList
			}
		case pathName::moreGlob => {
				var filter = new RegExpFilenameFilter(false, pathName)
				var paths = base.list(filter)
				paths flatMap (p => resolve(new File(base, p), moreGlob))
			}
		}
	}
	
	def getStablePrefix(absPath:File):File = {
		var l1 = absPath.toString.replace("\\", "/").split('/').toList.filter(_ != ".")
		var root = l1.head
		var pl1 = l1.tail.takeWhile(str => !str.contains('*'))
		var sb = new StringBuilder
		sb ++= root
		pl1.foreach(s => { sb += '/'; sb ++= s })
		new File(sb.toString)
	}
}
