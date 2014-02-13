package se.lth.immun.chem;

public class PeptideFragment implements IMolecule {

	public char	fragmentType;
	public int		ordinal;
	public double	mass;
	public Peptide peptide;
	
	public PeptideFragment(char fragmentType, int ordinal, double mass, Peptide peptide) {
		this.fragmentType = fragmentType;
		this.ordinal = ordinal;
		this.mass = mass;
		this.peptide = peptide;
	}

	public ElementComposition getComposition() {
		return null;
	}
	public double monoisotopicMass() { return mass; }
	
	public boolean same(PeptideFragment pf) {
		return ordinal == pf.ordinal && fragmentType == pf.fragmentType;
	}

	private IsotopeDistribution _dist = null;
	public IsotopeDistribution getIsotopeDistribution() {
		if (_dist == null) {
			_dist = new IsotopeDistribution();
			if (fragmentType == 'b') {
				for (int i = 0; i < ordinal; i++)
					_dist.add(peptide.aminoAcids[i].getIsotopeDistribution());
			} else if (fragmentType == 'y') {
				for (int i = 0; i < ordinal; i++)
					_dist.add(peptide.aminoAcids[peptide.aminoAcids.length - i - 1].getIsotopeDistribution());
			}
		}
		return _dist;
	}
}
