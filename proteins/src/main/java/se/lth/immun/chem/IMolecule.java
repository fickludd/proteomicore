package se.lth.immun.chem;

public interface IMolecule {

	public ElementComposition getComposition();
	public double monoisotopicMass();
	public IsotopeDistribution getIsotopeDistribution();
}
