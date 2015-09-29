package se.lth.immun.mzml

import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ArrayBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before

import java.io.File
import java.io.FileReader
import java.io.BufferedReader
import java.io.StringWriter
import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter

class MzMLWriteTest {

	@Test
	def readWriteTinyPwiz = {
		var f = new File("target/test-classes/tiny.pwiz.1.1.mzML")
		
		var spectra = new ArrayBuffer[Spectrum]
		var chromatograms = new ArrayBuffer[Chromatogram]
		
		var dh = new MzMLDataHandlers(
				i => assertEquals(4, i), 
				(s:Spectrum) => spectra += s,
				i => assertEquals(2, i), 
				(c:Chromatogram) => chromatograms += c
			)
		var m = MzML.fromFile(new XmlReader(new BufferedReader(new FileReader(f))), dh)
		
		assert(m != null)
		
		
		var testOut = new File("target/test-classes/test.mzML")
		var w = XmlWriter(testOut, false)
		
		var dw = new MzMLDataWriters(
					spectra.length,
					sw => 
						for (s <- spectra) yield
							s.write(sw, null, None, None)
					,
					chromatograms.length,
					cw => 
						for (c <- chromatograms) yield
							c.write(cw, null)
				)
		
		m.write(w, dw, true)
		val original = scala.io.Source.fromFile(f).getLines.mkString("\n")
		val created = scala.io.Source.fromFile(testOut).getLines.mkString("\n")
			
		//for ((orig, creat) <- original.zip(created))
		//	assertEquals(orig, creat)
		
		assertEquals(original, created)
		
		/*new StringBuilder
		val br = new BufferedReader(new FileReader(f))
		while ({
			val line = br.readLine
			if (line != null) original ++= line + "\n"
			line != null && line != ""
		}) {}*/
		//assertEquals(original.toString, sw.toString)
	}
}