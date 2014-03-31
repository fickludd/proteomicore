package se.lth.immun.chem;

public class Molecule implements IMolecule {
	
	ElementComposition ec;
	
	public Molecule(ElementComposition ec) {
		this.ec = ec;
	}
	
	public Molecule(Element[] elements, int[] counts) {
		ec = new ElementComposition(elements, counts);
	}
	public ElementComposition getComposition() { return ec; }
	public double monoisotopicMass() { return ec.monoisotopicMass(); }
	public IsotopeDistribution getIsotopeDistribution() { return ec.getIsotopeDistribution(); }
}
