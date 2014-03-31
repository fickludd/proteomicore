package se.lth.immun.unimod;

import static org.junit.Assert.*;
import org.junit.Test;

import se.lth.immun.chem.Peptide;
import se.lth.immun.chem.Ion;

public class UniModTest {

	
	@Test
	public void p1() {
		String str = "NMITGTSQADC(UniMod:4)AILIIAGGVGEFEAGISK";
		Peptide p = UniMod.parseUniModSequence(str);
		double mz2 = 975.1562;
		
		assertEquals(mz2, Ion.mz(p, 3), 0.0001);
	}
	
	@Test
	public void p2() {
		String str = "Q(UniMod:28)IEELVEAIVLPMK";
		Peptide p = UniMod.parseUniModSequence(str);
		double mz2 = 797.9417;
		
		assertEquals(mz2, Ion.mz(p, 2), 0.0001);
	}
	
	@Test
	public void p3() {
		String str = "C(UniMod:26)AVVSAIGSAAFAAGSAFIPYFK";
		Peptide p = UniMod.parseUniModSequence(str);
		double mz2 = 1144.5746;
		
		assertEquals(mz2, Ion.mz(p, 2), 0.0001);
	}
	
	@Test
	public void p4() {
		String str = "NSVYM(UniMod:35)GSAAMAEFLADIALC(UniMod:4)PLEATR";
		Peptide p = UniMod.parseUniModSequence(str);
		double mz2 = 939.78;
		
		assertEquals(mz2, Ion.mz(p, 3), 0.01);
	}
	
	@Test
	public void p5() {
		String str = "E(UniMod:27)SLPLIVFLR";
		Peptide p = UniMod.parseUniModSequence(str);
		double mz2 = 584.8581;
		
		assertEquals(mz2, Ion.mz(p, 2), 0.0001);
	}
}
