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
		/*
		 Gold standard is
		 http://db.systemsbiology.net:8080/proteomicsToolkit/FragIonServlet?sequence=ARNDCQEGHILKMFPSTWYVX&massType=monoRB&charge=1&aCB=1&xCB=1&bCB=1&yCB=1&cCB=1&zCB=1&nterm=0.0&cterm=0.0&addModifType=&addModifVal=
		*/
		
		Peptide p = new Peptide(aas);
		EPeptideFragment[] fragmentTypes = {EPeptideFragment.a, EPeptideFragment.b, EPeptideFragment.c, EPeptideFragment.x, EPeptideFragment.y, EPeptideFragment.z};
		PeptideFragment[] fs = p.getFragments(fragmentTypes);
		
		assertEquals(20*6, fs.length);
		
		// a11
		assertEquals(1209.57991 - Constants.PROTON_WEIGHT, fs[60].mass, 0.001); 
		assertEquals(11, fs[60].ordinal);
		assertEquals(EPeptideFragment.a, fs[60].fragmentType);
		
		// b12
		assertEquals(1365.66978 - Constants.PROTON_WEIGHT, fs[67].mass, 0.001); 
		assertEquals(12, fs[67].ordinal);
		assertEquals(EPeptideFragment.b, fs[67].fragmentType);
		
		// c13
		assertEquals(1513.73682 - Constants.PROTON_WEIGHT, fs[74].mass, 0.001); 
		assertEquals(13, fs[74].ordinal);
		assertEquals(EPeptideFragment.c, fs[74].fragmentType);
		
		// x8
		assertEquals(1038.49370 - Constants.PROTON_WEIGHT, fs[75].mass, 0.001); 
		assertEquals(8, fs[75].ordinal);
		assertEquals(EPeptideFragment.x, fs[75].fragmentType);
		
		// y7
		assertEquals(865.44602 - Constants.PROTON_WEIGHT, fs[82].mass, 0.001); 
		assertEquals(7, fs[82].ordinal);
		assertEquals(EPeptideFragment.y, fs[82].fragmentType);
		
		// z6
		assertEquals(751.36671 - Constants.PROTON_WEIGHT, fs[89].mass, 0.001); 
		assertEquals(6, fs[89].ordinal);
		assertEquals(EPeptideFragment.z, fs[89].fragmentType);
	}
	
	@Test
	public void testToString() {
		Peptide p = new Peptide(aas);
		assertTrue("ARNDCQEGHILKMFPSTWYVX".equals(p.toString()));
	}
}
