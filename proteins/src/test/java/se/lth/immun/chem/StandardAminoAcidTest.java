package se.lth.immun.chem;

import static org.junit.Assert.*;

import org.junit.Test;

public class StandardAminoAcidTest {
	
	@Test
	public void mz() {
		assertEquals(57.021464, StandardAminoAcid.G.monoisotopicMass(), 0.0001f);
		assertEquals(71.037114, StandardAminoAcid.A.monoisotopicMass(), 0.0001f);
		assertEquals(87.032028, StandardAminoAcid.S.monoisotopicMass(), 0.0001f);
		assertEquals(97.052764, StandardAminoAcid.P.monoisotopicMass(), 0.0001f);
		assertEquals(99.068414, StandardAminoAcid.V.monoisotopicMass(), 0.0001f);

		assertEquals(101.047679, StandardAminoAcid.T.monoisotopicMass(), 0.0001f);
		assertEquals(113.084064, StandardAminoAcid.L.monoisotopicMass(), 0.0001f);
		assertEquals(113.084064, StandardAminoAcid.I.monoisotopicMass(), 0.0001f);
		assertEquals(114.042927, StandardAminoAcid.N.monoisotopicMass(), 0.0001f);
		assertEquals(115.026943, StandardAminoAcid.D.monoisotopicMass(), 0.0001f);

		assertEquals(128.094963, StandardAminoAcid.K.monoisotopicMass(), 0.0001f);
		assertEquals(128.058578, StandardAminoAcid.Q.monoisotopicMass(), 0.0001f);
		assertEquals(129.042593, StandardAminoAcid.E.monoisotopicMass(), 0.0001f);
		assertEquals(131.040485, StandardAminoAcid.M.monoisotopicMass(), 0.0001f);
		assertEquals(137.058912, StandardAminoAcid.H.monoisotopicMass(), 0.0001f);

		assertEquals(147.068414, StandardAminoAcid.F.monoisotopicMass(), 0.0001f);
		assertEquals(156.101111, StandardAminoAcid.R.monoisotopicMass(), 0.0001f);
		assertEquals(163.063329, StandardAminoAcid.Y.monoisotopicMass(), 0.0001f);
		assertEquals(186.079313, StandardAminoAcid.W.monoisotopicMass(), 0.0001f);
		assertEquals(103.009185, StandardAminoAcid.C.monoisotopicMass(), 0.0001f);
		assertEquals(113.084064, StandardAminoAcid.X.monoisotopicMass(), 0.0001f);
	}
}
