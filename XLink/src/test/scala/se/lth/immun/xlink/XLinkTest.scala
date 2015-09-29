package se.lth.immun.xlink

import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before

import java.io.StringReader
import java.io.IOException

import se.lth.immun.chem._

class XLinkTest {
	
	def parsePep(str:String):Peptide =
		new Peptide(str.map(StandardAminoAcid.fromChar).toArray)
	
	@Test
	def imp_DSS() = {
		assertEquals(138.06808, IMPXLink.XLINK_MAP("DSS").monoisotopicMass, 0.001)
		assertEquals(155.09462, IMPXLink.XLINK_MAP("DSS-n").monoisotopicMass, 0.001)
		assertEquals(156.07864, IMPXLink.XLINK_MAP("DSS-h").monoisotopicMass, 0.001)
	}
	
	@Test
	def imp_xlink1() = {
		val xlinkRes = IMPXLink.fromString("AGK[0]~DSS~GRTAK[4]", parsePep)
		//println(XLink.DSS.monoisotopicMass())
		//println(xlink.monoisotopicMass)
		
		xlinkRes match { case Left(xlink) => 
		assertEquals(XLink.DSS.monoisotopicMass() + parsePep("AGK").monoisotopicMass + 
				parsePep("GRTAK").monoisotopicMass, xlink.monoisotopicMass, 0.001)
		}
	}
	
	@Test
	def imp_xlink3() = {
		val xlinkRes = IMPXLink.fromString("AGK[0]~C(4) O(2) H(9)~GRTAK[5]", parsePep)
		
		val m = new ElementComposition(Array(Element.C, Element.O, Element.H), Array(4, 2, 9)).monoisotopicMass
		
		xlinkRes match { case Left(xlink) => 
			assertEquals(m + 274.1641052949671 + 531.3128947007899, xlink.monoisotopicMass, 0.00001)
		}
	}
	
	@Test
	def imp_looplink() = {
		val xlinkRes = IMPXLink.fromString("AGK[0:3]~DSS", parsePep)
		
		xlinkRes match { case Left(xlink) => 
			assertEquals(XLink.DSS.monoisotopicMass() +  
				parsePep("AGK").monoisotopicMass, xlink.monoisotopicMass, 0.001)
		}
	}
	/*
	 * This is not testable without the unimod library
	@Test
	def monolink() = {
		val xlink = parsePep("AGK(Unimod:1020)")
		
		assertEquals(XLink.DSS.monoisotopicMass() + XLink.WATER.monoisotopicMass() + 
				parsePep("AGK").monoisotopicMass, xlink.monoisotopicMass, 0.001)
	}
	*/
	@Test
	def imp_double_xlink() = {
		val xlinkRes = IMPXLink.fromString("AGK[0,3]~DSS~GRTAK[0,5]", parsePep)
		
		xlinkRes match { case Left(xlink) => 
			assertEquals(XLink.DSS.monoisotopicMass() + XLink.DSS.monoisotopicMass() + 
				parsePep("AGK").monoisotopicMass + parsePep("GRTAK").monoisotopicMass, 
				xlink.monoisotopicMass, 0.001)
		}
	}
	
	@Test
	def kojakSingle = {
		val xlinkRes = KojakXLink.fromString("-.HRHPDEAAFFDTASTGK[156.08]TFPGFFSPMLGEFVSETESR.-", KojakXLink.DSS_CARB_CONF)
		
		xlinkRes match { case Left(xlink) => 
			assert(xlink.positions.isEmpty)
			assertEquals(XLink.DSS.monoisotopicMass() + XLink.WATER.monoisotopicMass() + 
				parsePep("HRHPDEAAFFDTASTGKTFPGFFSPMLGEFVSETESR").monoisotopicMass, 
				xlink.monoisotopicMass, 0.001)
		}
	}
	
	@Test
	def kojakLoop = {
		val xlinkRes = KojakXLink.fromString("-.NSLFEYQKNNKDSHSLTTNIMEILR(8,11)-LOOP.-", KojakXLink.DSS_CARB_CONF)
		
		xlinkRes match { case Left(xlink) => 
			assertEquals(1, xlink.positions.length)
			assertEquals(XLink.DSS.monoisotopicMass() + 
				parsePep("NSLFEYQKNNKDSHSLTTNIMEILR").monoisotopicMass, 
				xlink.monoisotopicMass, 0.001)
		}
	}
	
	@Test
	def kojakCross = {
		val xlinkRes = KojakXLink.fromString("-.EVTKEVVTSEDGSDCPEAMDLGTLSGIGTLDGFR(4)--SCSKTVTK(4).-", KojakXLink.DSS_CARB_CONF)
		
		val carbAmidoMethylMass = 57.021464
		
		xlinkRes match { case Left(xlink) => 
			assertEquals(1, xlink.positions.length)
			assertEquals(
				XLink.DSS.monoisotopicMass() + 
				carbAmidoMethylMass * 2 +
				parsePep("EVTKEVVTSEDGSDCPEAMDLGTLSGIGTLDGFR").monoisotopicMass + 
				parsePep("SCSKTVTK").monoisotopicMass, 
				xlink.monoisotopicMass, 0.001)
		}
	}
	
	@Test
	def kojakCrossWithMono = {
		val xlinkRes = KojakXLink.fromString("-.LEKELEEK[155.09]K[156.08]EALELAIDQASR(3)--KMLEEIMK(1).-", KojakXLink.DSS_CARB_CONF)
		
		xlinkRes match { case Left(xlink) => 
			assertEquals(1, xlink.positions.length)
			assertEquals(XLink.DSS.monoisotopicMass()*3 + XLink.WATER.monoisotopicMass() + XLink.AMMONIUM.monoisotopicMass() +
				parsePep("LEKELEEKKEALELAIDQASR").monoisotopicMass + parsePep("KMLEEIMK").monoisotopicMass, 
				xlink.monoisotopicMass, 0.001)
		}
	}
}