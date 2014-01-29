package se.lth.immun.chem;

import static org.junit.Assert.*;

import org.junit.Test;

public class ElementCompositionTest {

	@Test
	public void monoisotopicMass() {
		assertEquals(
				Constants.WATER_WEIGHT, 
				Constants.WATER.getComposition().monoisotopicMass(), 
				0.00001f);
	}

	@Test
	public void join() {
		ElementComposition w = Constants.WATER.getComposition();
		ElementComposition e = w.join(w);
		assertEquals(
				2 * Constants.WATER_WEIGHT, 
				e.monoisotopicMass(), 
				0.00001f);

		int hi = 0;
		int oi = 0;
		while (e.elements[hi] != Element.H) hi++;
		while (e.elements[oi] != Element.O) oi++;
		assertEquals(4, e.counts[hi]);
		assertEquals(2, e.counts[oi]);
		assertEquals(Element.H, e.elements[hi]);
		assertEquals(Element.O, e.elements[oi]);
		
		Element[] se = {Element.S, Element.H};
		int[] sc = {2, 4};
		ElementComposition s = new ElementComposition(se, sc);
		e = w.join(s);
		
		hi = 0;
		oi = 0;
		int si = 0;
		while (e.elements[hi] != Element.H) hi++;
		while (e.elements[oi] != Element.O) oi++;
		while (e.elements[si] != Element.S) si++;
		assertEquals(6, e.counts[hi]);
		assertEquals(2, e.counts[si]);
		assertEquals(1, e.counts[oi]);
		
		ElementComposition e2 = s.join(w);
		hi = 0;
		oi = 0;
		si = 0;
		while (e2.elements[hi] != Element.H) hi++;
		while (e2.elements[oi] != Element.O) oi++;
		while (e2.elements[si] != Element.S) si++;
		assertEquals(6, e2.counts[hi]);
		assertEquals(2, e2.counts[si]);
		assertEquals(1, e2.counts[oi]);
	}
}
