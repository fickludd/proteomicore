package se.lth.immun.group

import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before

import java.io.StringReader

import se.lth.immun.xml.XmlReader

class GroupingFileTest extends AssertionsForJUnit {

	val valid = """<?xml version="1.0"?>
<groupings>
	<group name="proteins" count="4">
		<group name="Ribosomes" count="10" value="2.0">
			<group name="RL4_STRP1" />
			<group name="RL29_STRA3" />
			<group name="RL7_STRP1" />
			<group name="RL18_STRP1" />
			<group name="RS10_STRA1" />
			<group name="RL22_STRP1" />
			<group name="RL15_STRP1" />
			<group name="RL1_STRP1" />
			<group name="RS17_STRA1" />
			<group name="NP_269238.1" />
		</group>"
		<group name="FAS II">
			<group name="ACPS_STRP1" value="3.1" />
			<group name="ACP_STRP1" />
			<group name="FABH_STRP1" />
			<group name="FABZ_STRP1" />
			<group name="NP_269766.1" />
			<group name="NP_269767.1" />
			<group name="NP_269768.1" />
			<group name="NP_269770.1" />
			<group name="NP_269771.1" />
			<group name="NP_269772.1" />
			<group name="NP_269773.1" />
			<group name="NP_269774.1" />
			<group name="NP_269777.1" />
			<group name="NP_269778.1" />
		</group>"
		<group name="Virulome" count="29" type="dangerous">
			<group name="NP_269220.1" />
			<group name="STRP_STRP1" />
			<group name="C5AP_STRP1" />
			<group name="NP_269972.1" />
			<group name="DLTC_STRP1" />
			<group name="NP_268944.1" />
			<group name="NP_269065.1" />
			<group name="SPEC_STRP1" />
			<group name="NP_268627.1" />
			<group name="SPEB_STRP1" />
			<group name="NP_269973.1" />
			<group name="DLTA_STRP1" />
			<group name="NP_269464.1" />
			
			<group name="17981973" />
			<group name="NP_268729.1" />
			<group name="NP_268723.1" />
			<group name="SPEH_STRP1" />
			<group name="NP_269959.1" />
			<group name="NP_268735.1" />
			<group name="SPEG_STRP1" />
			<group name="TACY_STRP1" />
			<group name="NP_269203.1" />
			<group name="NP_269818.1" />
			<group name="NP_269947.1" />
			<group name="NP_269402.1" />
			<group name="NP_268544.1" />
			<group name="NP_269520.1" />
			<group name="HASA_STRP1" />
			<group name="NP_269989.1" />
		</group>"
		<group name="ADH" count="1">
			<group name="" />
		</group>
	</group>
</groupings>"""
	
	val notEnded = """<?xml version="1.0"?>
<groupings>
	<group name="proteins" count="4">
		<group name="Ribosomes" count="10" value="2.0">
			<group name="RL4_STRP1" >
			<group name="RL29_STRA3" />
		</group>
	</group>
</groupings>"""
		
	var x:XmlReader = null
	var s:StringReader = null

	@Test
	def parseValid = {
		s = new StringReader(valid)
		x = new XmlReader(s)
		
		try {
			var gf = new GroupingFile(x)
			
			assertNotNull(gf.root)
			var r = gf.root
			assertEquals("proteins", r.name)
			assertEquals(4, r.children.length)
			
			val rib = r.children(0)
			assertEquals("Ribosomes", rib.name)
			assertEquals(2.0, rib.value, 0.000001)
			
			val fas = r.children(1)
			assertEquals("FAS II", fas.name)
			val acps = fas.children(0)
			assertEquals("ACPS_STRP1", acps.name)
			assertEquals(3.1, acps.value, 0.000001)
			
			var v = r.children(2)
			assertEquals("Virulome", v.name)
			assertEquals("dangerous", v.metaType)
			assertEquals(29, v.children.length)
			
			var c5ap = v.children(2)
			assertEquals("C5AP_STRP1", c5ap.name)
			assertEquals(0, c5ap.children.length)
		} catch {
			case e:Exception => fail("Threw exception for valid input: "+e.getLocalizedMessage)
		}
	}
	
	@Test
	def parseNotEnded = {
		s = new StringReader(notEnded)
		x = new XmlReader(s)
		
		try {
			var gf = new GroupingFile(x)
			fail("Didn't throw exception for unended xml input!")
		} catch {
			case e:Exception => 
				assertEquals("Invalid XML, element 'group name=\"RL4_STRP1\"' never ended!", e.getMessage)
			case x:Throwable =>
				fail("Threw strange exception: "+x)
		}
	}
}
