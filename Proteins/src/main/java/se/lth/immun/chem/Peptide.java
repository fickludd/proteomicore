package se.lth.immun.chem;

import java.util.HashMap;
import java.util.Map;

public class Peptide implements IMolecule {

	
	
	public final IAminoAcid[] 	aminoAcids;
	public final IMolecule 	nTermModification;
	public final IMolecule 	cTermModification;
	
	
	
	public Peptide(IAminoAcid[] aminoAcids) {
		this.aminoAcids = aminoAcids;
		this.nTermModification = null;
		this.cTermModification = null;
	}

	
	
	public Peptide(
			IAminoAcid[] aminoAcids, 
			IMolecule nTermModification,
			IMolecule cTermModification
	) {
		this.aminoAcids = aminoAcids;
		this.nTermModification = nTermModification;
		this.cTermModification = cTermModification;
	}

	
	
	public ElementComposition getComposition() { 
		HashMap<Element, Integer> hm = new HashMap<Element, Integer>();
		
		// add terminal WATER
		hm.put(Element.H, 2);
		hm.put(Element.O, 1);
		
		for (int i = 0; i < aminoAcids.length; i++) {
			ElementComposition e = aminoAcids[i].getComposition();
			for (int j = 0; j < e.elements.length; j++)
				if (hm.containsKey(e.elements[j])) hm.put(e.elements[j], hm.get(e.elements[j]) + e.counts[j]);
				else hm.put(e.elements[j], e.counts[j]);
		}
		Element[] eret 	= new Element[hm.size()];
		int[] cret 		= new int[eret.length];
		int i = 0;
		for (Map.Entry<Element, Integer> entry: hm.entrySet()) {
			eret[i] = entry.getKey();
			cret[i] = entry.getValue();
			i++;
		}
		return new ElementComposition(eret, cret);
	}
	
	
	
	public double monoisotopicMass() {
		double mass = Constants.WATER_WEIGHT;
		for (IAminoAcid a : aminoAcids) 
			mass += a.monoisotopicMass();
		if (nTermModification != null) mass += nTermModification.monoisotopicMass();
		if (cTermModification != null) mass += cTermModification.monoisotopicMass();
		return mass;
	}
	
	
	
	public PeptideFragment[] getFragments(EPeptideFragment[] fragments) {
		PeptideFragment[] f = new PeptideFragment[(aminoAcids.length-1) * fragments.length];
		double[] masses = new double[aminoAcids.length];
		for (int i = 0; i < aminoAcids.length; i++) {
			masses[i] = aminoAcids[i].getComposition().monoisotopicMass();
		}
		
		for (int i = 0; i < fragments.length; i++) {
			EPeptideFragment fragment = fragments[i];
			for (int j = 1; j < aminoAcids.length; j++) {
				double mass = 0.0;
				switch (fragment) {
					case a:
					case c:
					case b:
						for (int k = 0; k < j; k++)
							mass += masses[k];
						if (nTermModification != null)
							mass += nTermModification.getComposition().monoisotopicMass();
						break;
					case x:
					case z:
					case y:
						for (int k = 0; k < j; k++)
							mass += masses[masses.length - k - 1];
						mass += Constants.WATER_WEIGHT;
						if (cTermModification != null)
							mass += cTermModification.getComposition().monoisotopicMass();
						break;
				}
				
				f[i * (aminoAcids.length-1) + (j-1)] = 
						new PeptideFragment(fragment.type, j, mass, this);
			}
		}
		
		return f;
	}
	
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (IAminoAcid a : aminoAcids)
			sb.append(a.toString());
		return sb.toString();
	}
	
	
	
	private IsotopeDistribution _dist = null;
	public IsotopeDistribution getIsotopeDistribution() {
		if (_dist == null) {
			_dist = Constants.WATER.getIsotopeDistribution().copy();
			for (IAminoAcid a : aminoAcids)
				_dist.add(a.getIsotopeDistribution());
		}
		return _dist;
	}
	
	

	public static final Element[] AVERAGINE_ELEMENTS = {Element.C, Element.H, Element.O, Element.N, Element.S};
	public static final double[] AVERAGINE_OCCURENCE = {4.9384, 7.7583, 1.4773, 1.3577, 0.0417}; 
	public static final double AVERAGINE_MASS = 111.1254;
	
	public static ElementComposition averagine(double mass) {
		double n = mass / AVERAGINE_MASS;
		int[] counts = new int[5];
		for (int i = 0; i<5; i++)
			counts[i] = (int)Math.round(AVERAGINE_OCCURENCE[i] * n);
		return new ElementComposition(AVERAGINE_ELEMENTS, counts);
	}
	
	
	
	/**
	 * These seem roughly ok when visually compared to expasy tool IsotopIdent
	 * http://education.expasy.org/student_projects/isotopident/htdocs/
	 */
	public static void main(String[] args) {
		
		long before = System.currentTimeMillis();
		
		System.out.println("ADGGCELSQL");
		StandardAminoAcid[] aa = {
				StandardAminoAcid.A,
				StandardAminoAcid.D,
				StandardAminoAcid.G,
				StandardAminoAcid.G,
				StandardAminoAcid.C,
				StandardAminoAcid.E,
				StandardAminoAcid.L,
				StandardAminoAcid.S,
				StandardAminoAcid.Q,
				StandardAminoAcid.L};
		Peptide p = new Peptide(aa);
		IsotopeDistribution i = p.getIsotopeDistribution();
		for (int j = 0; j < i.dm; j++) {
			System.out.println((i.m0 + j) + " \t"+i.intensities[j]);
		}
		
		System.out.println(" time taken: "+(System.currentTimeMillis() - before)+" ms");
		
		

		System.out.println("CESQLK");
		StandardAminoAcid[] CESQLK = {
				StandardAminoAcid.C,
				StandardAminoAcid.E,
				StandardAminoAcid.S,
				StandardAminoAcid.Q,
				StandardAminoAcid.L,
				StandardAminoAcid.K};
		p = new Peptide(CESQLK);
		i = p.getIsotopeDistribution();
		for (int j = 0; j < i.dm; j++) {
			System.out.println((i.m0 + j) + " \t"+i.intensities[j]);
		}
	}
}
