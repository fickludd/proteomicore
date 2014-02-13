package se.lth.immun.chem;

public class Ion<T extends IMolecule> {
	
	public T 		molecule;
	public int 	numExtraProtons		= 0;
	public int 	numExtraElectrons	= 0;
	
	public Ion(T molecule, int numExtraProtons) {
		this.molecule = molecule;
		this.numExtraProtons = numExtraProtons;
	}
	
	public Ion(T molecule, int numExtraProtons, int numExtraElectrons) {
		this.molecule = molecule;
		this.numExtraProtons = numExtraProtons;
		this.numExtraElectrons = numExtraElectrons;
	}
	
	public double mz() {
		double mass = molecule.monoisotopicMass();
		mass += numExtraProtons * Constants.PROTON_WEIGHT + numExtraElectrons * Constants.ELECTRON_WEIGHT;
		return mass / Math.abs(numExtraProtons - numExtraElectrons);
	}
	
	public int sign() {
		return numExtraProtons > numExtraElectrons ? 1 : -1;
	}
	
	public static double mz(IMolecule molecule, int numExtraProtons) {
		return mz(molecule, numExtraProtons, 0);
	}
	public static double mz(IMolecule molecule, int numExtraProtons, int numExtraElectrons) {
		double mass = molecule.monoisotopicMass();
		mass += numExtraProtons * Constants.PROTON_WEIGHT + numExtraElectrons * Constants.ELECTRON_WEIGHT;
		return mass / Math.abs(numExtraProtons - numExtraElectrons);
	}
}
