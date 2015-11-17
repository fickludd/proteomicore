package se.lth.immun.traml.clear

import org.junit.Assert._
import org.junit.Test

import java.io.File
import java.io.FileReader
import java.io.BufferedReader

import se.lth.immun.xml.XmlReader

import Clear._
import ClearRetentionTime._

class ClearTraMLTest {

	val RT_TRAML = new File("src/test/resources/RT.TraML")
	
	@Test
	def readFile = {
		val ct = ClearTraML.fromFile(new XmlReader(new BufferedReader(new FileReader(RT_TRAML))))
		
		assert(ct.proteins("1/sp|P55264|ADK_MOUSE"))
		assert(ct.proteins("sp|P00688|AMYP_MOUSE"))
		
		assert(ct.compounds.exists(_.id == "AAADEPKPK"))
		assert(ct.compounds.exists(_.id == "HMWPGDIK"))
		assert(ct.compounds.exists(_.id == "AADAHVDAHYYEQNEQPTGTC(UniMod:4)AAC(UniMod:4)ITGGNR"))
		
		assertEquals(4, ct.compounds.size)
		
		val pepIRT = ct.compounds.find(_.id == "HMWPGDIK").get
		assert(pepIRT.rt.nonEmpty)
		pepIRT.rt match {
			case Some(IRT(t)) =>
				assertEquals(19.2447, t, 0.0001)
			case Some(_) =>
				fail("Found wrong kind of RT")
			case None =>
				fail("Found no rt info")
		}
		assertEquals(1, pepIRT.assays.length)
		val assay = pepIRT.assays.head
		assertEquals(6, assay.ms2Channels.length)
		assertEquals(2, assay.z)
		assertEquals(492.24057, assay.mz, 0.0001)
		assert(assay.ce.nonEmpty)
		assertEquals(30.0, assay.ce.get, 0.0001)
		
		val t1 = assay.ms2Channels.find(_.id == "y5").get
		assertEquals(529.29803873, t1.mz, 0.00001)
		assertEquals(1, t1.z)
		assertEquals(0.62309080, t1.expIntensity.get, 0.00001)
		
		val pepNTimeRT = ct.compounds.find(_.id == "AAADEPKPK").get
		assert(pepNTimeRT.rt.nonEmpty)
		pepNTimeRT.rt match {
			case Some(Ntime(t)) =>
				assertEquals(-33.9, t, 0.1)
			case Some(_) =>
				fail("Found wrong kind of RT")
			case None =>
				fail("Found no rt info")
		}
		
		assertEquals(1, pepNTimeRT.assays.length)
		val assay2 = pepNTimeRT.assays.head
		assertEquals(6, assay2.ms2Channels.length)
		assertEquals(485.253631, assay2.mz, 0.00001)
		assertEquals(2, assay2.z)
		
		val t2 = assay2.ms2Channels.find(_.id == "y4/-0.001").get
		assertEquals(469.31329, t2.mz, 0.00001)
		assertEquals(1, t2.z)
		assertEquals(4930.3, t2.expIntensity.get, 0.01)
	}
}