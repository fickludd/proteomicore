package se.lth.immun.chem;

import static org.junit.Assert.*;

import org.junit.Test;

public class IonTest {
	
	@Test
	public void mz() {
		Ion<IMolecule> i = new Ion<IMolecule>(Constants.WATER, 1);
		assertEquals(
				Constants.WATER_WEIGHT + 1.00727646688, 
				i.mz(), 
				Double.MIN_VALUE);

		i = new Ion<IMolecule>(Constants.WATER, 0, 1);
		assertEquals(
				Constants.WATER_WEIGHT + 0.00054857991, 
				i.mz(), 
				Double.MIN_VALUE);

		i = new Ion<IMolecule>(Constants.WATER, 3);
		assertEquals(
				(Constants.WATER_WEIGHT + 3 * 1.00727646688) / 3, 
				i.mz(), 
				Double.MIN_VALUE);
	}
}
