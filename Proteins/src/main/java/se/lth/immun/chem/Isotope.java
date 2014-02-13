package se.lth.immun.chem;

public class Isotope {

	String 	symbol;
	int 	massNumber;
	
	/**
	 * http://www.nist.gov/pml/data/comp-notes.cfm:
	 * 
	 * "These values are scaled to Ar(12C) = 12, where 12C is a neutral atom in 
	 * its nuclear and electronic ground state. Thus, the relative atomic mass 
	 * of entity X is given by: Ar(X) = m(X) / [m(12C) / 12] . If # is present, 
	 * the value and error were derived not from purely experimental data, but 
	 * at least partly from systematic trends. The 2003 Atomic Mass Evaluation 
	 * does not extend beyond an atomic mass of 293."
	 */
	double	relAtomMass;
	
	/**
	 * http://www.nist.gov/pml/data/comp-notes.cfm:
	 * 
	 * "Mole fraction of the various isotopes
	 * 
     * In the opinion of the Subcommittee for Isotopic Abundance Measurements (SIAM), 
     * these values represent the isotopic composition of the chemicals and/or 
     * materials most commonly encountered in the laboratory. They may not, 
     * therefore, correspond to the most abundant natural material. The uncertainties 
     * listed in parenthesis cover the range of probable variations of the materials 
     * as well as experimental errors. These values are consistent with the values 
     * published in Atomic Weights of the Elements, 2001."
	 */
	double	occurence;
	
	
	public Isotope(String symbol, int massNumber, double relAtomMass,
			double occurence) {
		super();
		this.symbol = symbol;
		this.massNumber = massNumber;
		this.relAtomMass = relAtomMass;
		this.occurence = occurence;
	}
}
