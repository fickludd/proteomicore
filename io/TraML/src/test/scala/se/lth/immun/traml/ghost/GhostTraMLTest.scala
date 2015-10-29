package se.lth.immun.traml.ghost

import org.junit.Assert._
import org.junit.Test

import java.io.File
import java.io.FileReader
import java.io.BufferedReader

import se.lth.immun.xml.XmlReader

import Ghost._
import GhostRetentionTime._

class GhostTraMLTest {

	val RT_TRAML = new File("src/test/resources/RT.TraML")
	
	@Test
	def readFile = {
		val gt = GhostTraML.fromFile(new XmlReader(new BufferedReader(new FileReader(RT_TRAML))))
		
		assert(gt.proteins.contains("1/sp|P55264|ADK_MOUSE"))
		assert(gt.proteins.contains("sp|P00688|AMYP_MOUSE"))
		
		assert(gt.peptides.contains("1_AAADEPKPK_2"))
		assert(gt.peptides.contains("HMWPGDIK"))
		assert(gt.peptides.contains("11_AADAHVDAHYYEQNEQPTGTC(UniMod:4)AAC(UniMod:4)ITGGNR_4"))
		
		assertEquals(3, gt.transitionGroups.size)
		assert(gt.transitionGroups.forall(_._2.length == 6))
		
		val transRT = gt.transitions.filter(_.peptide.map(_.id == "HMWPGDIK").getOrElse(false))
		for (t <- transRT) {
			assert(t.rt.nonEmpty)
			t.rt match {
				case Some(IRT(t)) =>
					assertEquals(19.2447, t, 0.0001)
				case Some(_) =>
					fail("Found wrong kind of RT")
				case None =>
					fail("Found no rt info")
			}
		}
		
		val pepNTimeRT = gt.transitions.filter(_.peptide.map(_.id == "1_AAADEPKPK_2").getOrElse(false))
		for (t <- pepNTimeRT) {
			assert(t.rt.nonEmpty)
			t.rt match {
				case Some(Ntime(t)) =>
					assertEquals(-33.9, t, 0.1)
				case Some(_) =>
					fail("Found wrong kind of RT")
				case None =>
					fail("Found no rt info")
			}
		}
		
		val t1 = gt.transitions.find(_.id == "HMWPGDIK++ y5+").get
		assert(t1.ce.nonEmpty)
		assertEquals(30.0, t1.ce.get, 0.0001)
		assertEquals(492.240570, t1.q1, 0.00001)
		assertEquals(529.29803873, t1.q3, 0.00001)
		assertEquals(2, t1.q1z)
		assertEquals(1, t1.q3z)
		assertEquals(1, t1.ions.length)
		assertEquals("y5", t1.ions.head)
		assertEquals(0.62309080, t1.intensity.get, 0.00001)
		
		val t2 = gt.transitions.find(_.id == "5_AAADEPKPK_2").get
		assertEquals(485.253631, t2.q1, 0.00001)
		assertEquals(469.31329, t2.q3, 0.00001)
		assertEquals(1, t2.q3z)
		assertEquals(4930.3, t2.intensity.get, 0.01)
		assertEquals(1, t2.ions.length)
		assertEquals("y4/-0.001", t2.ions.head)
	}
}