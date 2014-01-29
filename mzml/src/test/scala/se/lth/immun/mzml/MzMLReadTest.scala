package se.lth.immun.mzml

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

class MzMLReadTest {

	@Test
	def readTinyPwiz = {
		var f = new File("target/test-classes/tiny.pwiz.1.1.mzML")
		var dh = new MzMLDataHandlers(
				i => assertEquals(4, i), 
				(s:Spectrum) => {
					s.index match {
						case 0 => {
							assertEquals("scan=19", s.id)
							assertEquals(15, s.defaultArrayLength)
							assertEquals(7, s.cvParams.length)
							assertEquals(1, s.scanList.get.scans.length)
							assertEquals(2, s.binaryDataArrays.length)
							assertEquals(3, s.binaryDataArrays(0).cvParams.length)
							assertEquals("AAAAAAAALkAAAAAAAAAsQAAAAAAAACpAAAAAAAAAKEAAAAAAAAAmQAAAAAAAACRAAAAAAAAAIkAAAAAAAAAgQAAAAAAAABxAAAAAAAAAGEAAAAAAAAAUQAAAAAAAABBAAAAAAAAACEAAAAAAAAAAQAAAAAAAAPA/", s.binaryDataArrays(1).binary)
						}
						case 1 => {
							assert(!s.precursors.isEmpty)
							var p = s.precursors(0)
							assert(!p.selectedIons.isEmpty)
							var si = p.selectedIons(0)
							assert(si.cvParams.exists(_.accession == "MS:1000744"))
						}
						case _ => {}
					} 
				},
				i => assertEquals(2, i), 
				(c:Chromatogram) => {
					c.index match {
						case 1 => {
							assertEquals("sic", c.id)
							assertEquals(10, c.defaultArrayLength)
							assertEquals("MS:1000627", c.cvParams(0).accession)
							assert(c.precursor.isDefined)
							assertEquals("MS:1000133", c.precursor.get.activation.cvParams(0).accession)
							assertEquals(108, c.binaryDataArrays(0).encodedLength)
						}
						case _ => {}
					}
				})
		var m = MzML.fromFile(new XmlReader(new BufferedReader(new FileReader(f))), dh)
		
		assert(m != null)
		assertEquals("urn:lsid:psidev.info:mzML.instanceDocuments.tiny.pwiz", m.id.get)
		assertEquals("1.1.0", m.version)
		
		assertEquals(2, m.cvs.length)
		var fd = m.fileDescription
		assertEquals(2, fd.fileContent.cvParams.length)
		assertEquals(3, fd.sourceFiles.length)
		assertEquals(1, fd.contacts.length)
		assertEquals(5, fd.contacts(0).cvParams.length)
		
		assertEquals(2, m.referenceableParamGroups.length)
		assertEquals(1, m.samples.length)
		assertEquals(3, m.softwares.length)
		assertEquals("CompassXtract", m.softwares(2).id)
		assertEquals(1, m.scanSettings.length)
		assertEquals(1, m.instrumentConfigurations.length)
		assertEquals(2, m.dataProcessings.length)
		
		assertEquals("Experiment_x0020_1", m.run.id)
		assertEquals("pwiz_processing", m.run.spectrumList.get.defaultDataProcessingRef)
	}

	@Test
	def readSmallZlibPwiz = {
		var f = new File("target/test-classes/small_zlib.pwiz.1.1.mzML")
		var dh = new MzMLDataHandlers(i => {}, s => {}, i => {}, c => {})
		var m = MzML.fromFile(new XmlReader(new BufferedReader(new FileReader(f))), dh)
		
		assert(m != null)
	}

	@Test
	def readNeutralLoss = {
		var f = new File("target/test-classes/neutral_loss_example_1.1.0.mzML")
		var dh = new MzMLDataHandlers(i => {}, s => {}, i => {}, c => {})
		var m = MzML.fromFile(new XmlReader(new BufferedReader(new FileReader(f))), dh)
		
		assert(m != null)
	}

	@Test
	def readPrecursorSpectrum = {
		var f = new File("target/test-classes/precursor_spectrum_example_1.1.0.mzML")
		var dh = new MzMLDataHandlers(i => {}, s => {}, i => {}, c => {})
		var m = MzML.fromFile(new XmlReader(new BufferedReader(new FileReader(f))), dh)
		
		assert(m != null)
	}

	@Test
	def readMRM = {
		var f = new File("target/test-classes/MRM_example_1.1.0.mzML")
		var dh = new MzMLDataHandlers(i => {}, s => {}, i => {}, c => {})
		var m = MzML.fromFile(new XmlReader(new BufferedReader(new FileReader(f))), dh)
		
		assert(m != null)
	}

	@Test
	def readIMzML = {
		val f = new File("target/test-classes/Experiment 1.imzML")
		val df = new File("target/test-classes/Experiment 1.ibd")
		val fc = new FileInputStream(df).getChannel
		var dh = new MzMLDataHandlers(
				i => {}, 
				s => {
					//println(s.binaryDataArrays.head.extBinary.array.mkString(" "))
				}, 
				i => {}, 
				c => {})
		
		//println("before: " +fc.position)
		var m = MzML.fromFile(
				new XmlReader(new BufferedReader(new FileReader(f))), 
				dh,
				fc)
		
		assert(m != null)
		//println("after: "+ fc.position)
		assert(fc.position > 0)
	}
}