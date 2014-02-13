package se.lth.immun.chem;

public interface IModifier {

	public boolean isTarget(IAminoAcid left, IAminoAcid x, IAminoAcid y);
	public IMolecule modification();
	public String modStr();
}
