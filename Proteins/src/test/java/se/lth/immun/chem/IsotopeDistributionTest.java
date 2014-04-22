package se.lth.immun.chem;

import static org.junit.Assert.*;

import org.junit.Test;

public class IsotopeDistributionTest {

	@Test
	public void element() {
		IsotopeDistribution i = Element.C.isotopeDistribution;

		assertEquals(12, i.m0);
		assertEquals(3, i.dm);
		assertEquals(0.9893, i.intensities[0], Double.MIN_VALUE);
		assertEquals(0.0107, i.intensities[1], Double.MIN_VALUE);
		assertEquals(0.0, i.intensities[2], Double.MIN_VALUE);
	}
	


	@Test
	public void water() {
		IsotopeDistribution i = Constants.WATER.getIsotopeDistribution();

		assertEquals(18, i.m0);
		assertEquals(3, i.dm);

		assertEquals(i.intensities[0], 0.99734f, 0.00001);
		assertEquals(i.intensities[3], 0.0f, 0.00001);
	}

	private void testAA(StandardAminoAcid aa, int m0) {
		IsotopeDistribution i = aa.getIsotopeDistribution();
		assertEquals(m0, i.m0, 0.00001f);
	}
	
	@Test
	public void aminoAcids() {
		IsotopeDistribution i = StandardAminoAcid.A.getIsotopeDistribution();
		assertEquals(4, i.dm);
		assertEquals(i.intensities[0], 0.961820341f, 0.00001);

		testAA(StandardAminoAcid.A, 71);
		testAA(StandardAminoAcid.R, 156);
		testAA(StandardAminoAcid.N, 114);
		testAA(StandardAminoAcid.D, 115);
		testAA(StandardAminoAcid.C, 103);
		testAA(StandardAminoAcid.Q, 128);
		testAA(StandardAminoAcid.E, 129);
		testAA(StandardAminoAcid.G, 57);
		testAA(StandardAminoAcid.H, 137);
		testAA(StandardAminoAcid.I, 113);
		testAA(StandardAminoAcid.L, 113);
		testAA(StandardAminoAcid.K, 128);
		testAA(StandardAminoAcid.M, 131);
		testAA(StandardAminoAcid.F, 147);
		testAA(StandardAminoAcid.P, 97);
		testAA(StandardAminoAcid.S, 87);
		testAA(StandardAminoAcid.T, 101);
		testAA(StandardAminoAcid.W, 186);
		testAA(StandardAminoAcid.Y, 163);
		testAA(StandardAminoAcid.V, 99);
		testAA(StandardAminoAcid.X, 113);
	}
	
	


	@Test
	public void peptide() {

		StandardAminoAcid[] aa = {
				StandardAminoAcid.A,
				StandardAminoAcid.D,
				StandardAminoAcid.G,
				StandardAminoAcid.G,
				StandardAminoAcid.C,
				StandardAminoAcid.E,
				StandardAminoAcid.L,
				StandardAminoAcid.S,
				StandardAminoAcid.Q,
				StandardAminoAcid.L};
		Peptide p = new Peptide(aa);
		IsotopeDistribution i = p.getIsotopeDistribution();
				
		assertEquals(991, i.m0, 0.00001);
		assertEquals(0.5712685151670263, i.intensities[0],0.0000001);
		assertEquals(0.27640646881744513, i.intensities[1], 0.0000001);
		assertEquals(0.11102047603942766, i.intensities[2], 0.0000001);
		assertEquals(0.031946667, i.intensities[3], 0.0000001);
		assertEquals(0.0075488477, i.intensities[4],  0.0000001);
		assertEquals(0.0014957594, i.intensities[5], 0.0000001);
		assertEquals(2.5694395E-4, i.intensities[6], 0.0000001);
		assertEquals(3.895941E-5, i.intensities[7], 0.0000001);
		assertEquals(3.9448983E-6, i.intensities[8], 0.0000001);
	}
}
