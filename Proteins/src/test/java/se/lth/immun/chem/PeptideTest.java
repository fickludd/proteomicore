package se.lth.immun.chem;

import static org.junit.Assert.*;

import org.junit.Test;

public class PeptideTest {

	StandardAminoAcid[] aas = {
			StandardAminoAcid.A, StandardAminoAcid.R, 
			StandardAminoAcid.N, StandardAminoAcid.D, 
			StandardAminoAcid.C, StandardAminoAcid.Q, 
			StandardAminoAcid.E, StandardAminoAcid.G, 
			StandardAminoAcid.H, StandardAminoAcid.I, 
			StandardAminoAcid.L, StandardAminoAcid.K, 
			StandardAminoAcid.M, StandardAminoAcid.F, 
			StandardAminoAcid.P, StandardAminoAcid.S, 
			StandardAminoAcid.T, StandardAminoAcid.W, 
			StandardAminoAcid.Y, StandardAminoAcid.V,
			StandardAminoAcid.X};
	
	@Test
	public void getComposition() {
		Peptide p = new Peptide(aas);
		
		ElementComposition e = p.getComposition();

		int hi = 0;
		int oi = 0;
		int ci = 0;
		int ni = 0;
		int si = 0;
		while (e.elements[hi] != Element.H) hi++;
		while (e.elements[oi] != Element.O) oi++;
		while (e.elements[ni] != Element.N) ni++;
		while (e.elements[ci] != Element.C) ci++;
		while (e.elements[si] != Element.S) si++;
		assertEquals(113, e.counts[ci]);
		assertEquals(170, e.counts[hi]); // remember terminal water
		assertEquals(30, e.counts[ni]);
		assertEquals(31, e.counts[oi]); // remember terminal water
		assertEquals(2, e.counts[si]);
		assertEquals(Element.C, e.elements[ci]);
		assertEquals(Element.H, e.elements[hi]);
		assertEquals(Element.N, e.elements[ni]);
		assertEquals(Element.O, e.elements[oi]);
		assertEquals(Element.S, e.elements[si]);
	}
	
	@Test
	public void getFragments() {
		Peptide p = new Peptide(aas);
		EPeptideFragment[] fragmentTypes = {EPeptideFragment.a, EPeptideFragment.b, EPeptideFragment.c, EPeptideFragment.x, EPeptideFragment.y, EPeptideFragment.z};
		PeptideFragment[] fs = p.getFragments(fragmentTypes);
		
		assertEquals(20*6, fs.length);
		assertEquals(1209.57991 - Constants.PROTON_WEIGHT, fs[60].mass, 0.001); // a11
		assertEquals(1365.66978 - Constants.PROTON_WEIGHT, fs[67].mass, 0.001); // b12
		assertEquals(1513.73682 - Constants.PROTON_WEIGHT, fs[74].mass, 0.001); // c13
		assertEquals(1038.49370 - Constants.PROTON_WEIGHT, fs[75].mass, 0.001); // x8
		assertEquals(865.44602 - Constants.PROTON_WEIGHT, fs[82].mass, 0.001); // y9
		assertEquals(751.36671 - Constants.PROTON_WEIGHT, fs[89].mass, 0.001); // z10
	}
	
	@Test
	public void testToString() {
		Peptide p = new Peptide(aas);
		assertTrue("ARNDCQEGHILKMFPSTWYVX".equals(p.toString()));
	}
}
