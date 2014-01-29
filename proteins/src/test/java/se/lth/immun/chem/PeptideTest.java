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
		EPeptideFragment[] fragmentTypes = {EPeptideFragment.y, EPeptideFragment.b};
		PeptideFragment[] fs = p.getFragments(fragmentTypes);
		
		assertEquals(20*2, fs.length);
		assertEquals(456.20809, fs[23].mass, 0.00001);
		assertEquals(579.305674684, fs[3].mass, 0.00001);
	}
	
	@Test
	public void testToString() {
		Peptide p = new Peptide(aas);
		assertTrue("ARNDCQEGHILKMFPSTWYVX".equals(p.toString()));
	}
}
