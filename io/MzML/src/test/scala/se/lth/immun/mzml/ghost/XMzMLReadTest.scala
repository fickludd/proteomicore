package se.lth.immun.mzml.ghost

import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before

import java.io.File
import java.io.FileReader
import java.io.FileInputStream
import java.io.BufferedReader
import se.lth.immun.xml.XmlReader

class XMzMLReadTest {

	val normalFile 	= new File("target/test-classes/101112_JT_pl6_13.mzML")
	val normalX 	= XMzML.fromFile(new XmlReader(new BufferedReader(new FileReader(normalFile))))
	
	def checkSimilar(ref:XMzML, x:XMzML) = {
		
		for (q1 <- ref.grouper.q1s) {
			val rg = ref.grouper.extractGroup(q1).get
			val g = x.grouper.extractGroup(q1).get
			
			for ((rc, c) <- rg.chromatograms.sortBy(_.q3) zip g.chromatograms.sortBy(_.q3)) {
				assertEquals(rc.q3, c.q3, 0.0001)
				val intensityError = rc.intensities.zip(c.intensities).map(t => math.abs(t._1 - t._2) / ((t._1 + t._2)/2)).max
				assert(intensityError < 0.1)
				
				val timeError = rc.times.zip(c.times).map(t => math.abs(t._1 - t._2)).max
				assert(timeError < 0.1)
			}
		}
	}
	
	@Test
	def readNormal = {
		val f = new File("target/test-classes/101112_JT_pl6_13.mzML")
		val x = XMzML.fromFile(new XmlReader(new BufferedReader(new FileReader(f))))
	}
	
	@Test
	def readNumpressed = {
		val f = new File("target/test-classes/numpressed.mzML")
		val x = XMzML.fromFile(new XmlReader(new BufferedReader(new FileReader(f))))
		
		checkSimilar(normalX, x)
	}
	
	@Test
	def readZlibbed = {
		val f = new File("target/test-classes/zlib.mzML")
		val x = XMzML.fromFile(new XmlReader(new BufferedReader(new FileReader(f))))
		
		checkSimilar(normalX, x)
	}
	
	@Test
	def readNumpressedZlibbed = {
		val f = new File("target/test-classes/numprezzed.mzML")
		val x = XMzML.fromFile(new XmlReader(new BufferedReader(new FileReader(f))))
		
		checkSimilar(normalX, x)
	}

	@Test
	def readIMzML = {
		val f 	= new File("target/test-classes/101112_JT_pl6_13.imzML")
		val df 	= new File("target/test-classes/101112_JT_pl6_13.ibd")
		val fc 	= new FileInputStream(df).getChannel
		
		val x = XMzML.fromFile(new XmlReader(new BufferedReader(new FileReader(f))), true, fc)
		
		checkSimilar(normalX, x)
	}

	@Test
	def readNumpressedIMzML = {
		val f 	= new File("target/test-classes/numpressed.imzML")
		val df 	= new File("target/test-classes/numpressed.ibd")
		val fc 	= new FileInputStream(df).getChannel
		
		val x = XMzML.fromFile(new XmlReader(new BufferedReader(new FileReader(f))), true, fc)
		
		checkSimilar(normalX, x)
	}
}