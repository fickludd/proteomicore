package se.lth.immun.chem;

public class Constants {

	// the mass of a proton in unified atomic mass units
	public final static double PROTON_WEIGHT = 1.00727646688;
	// the mass of a neutron in unified atomic mass units
	public final static double NEUTRON_WEIGHT = 1.00866491560;
	// the mass of an electron in unified atomic mass units
	public final static double ELECTRON_WEIGHT = 0.00054857991;

	public final static double WATER_WEIGHT = 2*1.00782503207 + 15.99491461956;
	public final static IMolecule WATER = 
			new IMolecule() {
				final ElementComposition composition = new ElementComposition(new Element[]{Element.H,Element.O}, new int[]{2, 1});
				public ElementComposition getComposition() { return composition; }
				public double monoisotopicMass() {return 2*1.00782503207 + 15.99491461956; }

				private IsotopeDistribution _dist = null;
				public IsotopeDistribution getIsotopeDistribution() {
					if (_dist == null) {
						_dist = Element.O.isotopeDistribution.copy();
						_dist.add(Element.H.isotopeDistribution);
						_dist.add(Element.H.isotopeDistribution);
					}
					return _dist;
				}
			};
}
