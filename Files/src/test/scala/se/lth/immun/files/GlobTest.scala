package se.lth.immun.files

import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before

import java.io.File

class GlobTest extends AssertionsForJUnit {
	
	@Test
	def simpleGlob() = {
		var file = new File("src/test/resources/file1.txt")
		var paths = Glob.resolveGlob(file)
		assertEquals(1, paths.length)
		assertEquals(file.getAbsoluteFile.toString, paths(0).toString)
	}
	
	@Test
	def globalGlob() = {
		var absFile = (new File("src/test/resources/file2.png")).getAbsoluteFile()
		var paths = Glob.resolveGlob(absFile)
		assertEquals(1, paths.length)
		assertEquals(absFile.toString, paths(0).toString)
	}
	
	@Test
	def directoryGlob() = {
		var paths = Glob.resolveGlob(new File("src/test/resources")).map(_.toString)
		//assertEquals(3, paths.length)
		//assert(paths contains "src/test/resources/file1.txt")
		//assert(paths contains "src/test/resources/file2.png")
		//assert(paths contains "src/test/resources/file3.png")
	}
	
	@Test
	def complexGlob() = {
		var paths = Glob.resolveGlob(new File("src/test/resources/*.png")).map(_.toString)
		assertEquals(2, paths.length)
		//assert(paths contains "src/test/resources/file2.png")
		//assert(paths contains "src/test/resources/file3.png")
	}
}
