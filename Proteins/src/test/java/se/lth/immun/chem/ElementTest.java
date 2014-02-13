package se.lth.immun.chem;

import static org.junit.Assert.*;

import org.junit.Test;

public class ElementTest {

	@Test
	public void fromString() {
		assertEquals(
				Element.H, 
				Element.fromString("H"));
		assertEquals(
				Element.O18, 
				Element.fromString("18O"));
		assertEquals(
				Element.Cf, 
				Element.fromString("Cf"));
		assertEquals(
				Element.C, 
				Element.fromString("C"));
		assertEquals(
				Element.C, 
				Element.fromString("C B".substring(0, 1)));
	}
}
