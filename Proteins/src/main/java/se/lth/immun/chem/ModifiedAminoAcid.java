package se.lth.immun.chem;

public class ModifiedAminoAcid implements IAminoAcid {

	public IAminoAcid aa;
	public IMolecule modification;
	
	public ModifiedAminoAcid(IAminoAcid aa, IMolecule modification) {
		this.aa = aa;
		this.modification = modification;
	}

	public ElementComposition getComposition() {
		return aa.getComposition().join(modification.getComposition());
	}

	@Override
	public double monoisotopicMass() {
		return aa.monoisotopicMass() + modification.monoisotopicMass();//getComposition().monoisotopicMass();
	}

	private IsotopeDistribution _dist = null;
	public IsotopeDistribution getIsotopeDistribution() {
		if (_dist == null) {
			_dist = aa.getIsotopeDistribution().copy();
			_dist.add(modification.getIsotopeDistribution());
		}
		return _dist;
	}
	
	@Override
	public String toString() {
		return String.format("%s[%.3f]", aa.toString(), modification.monoisotopicMass());
	}
}
