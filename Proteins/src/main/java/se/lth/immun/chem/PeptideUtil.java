package se.lth.immun.chem;

import java.util.ArrayList;
import java.util.List;

public class PeptideUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Example usage of functions:
		String test = "ADGGCK";
		IAminoAcid[] aas = new IAminoAcid[test.length()]; 
		for (int ix=0;ix<test.length();ix++)
		{
			aas[ix]=StandardAminoAcid.fromChar(test.charAt(ix));
		}
		Peptide p = new Peptide(aas);
		ModifiedAminoAcid maa = new ModifiedAminoAcid(p.aminoAcids[1],Modifier.parseMolecule("H(2) C(3) N(1) O(1)"));
		p.aminoAcids[1] = maa;
		System.out.println(p.toString());
		EPeptideFragment[] fragments = {EPeptideFragment.y,	EPeptideFragment.b};
		PeptideFragment[] frags = p.getFragments(fragments);
		for (PeptideFragment pf:frags)
		{
			for (int charge=1;charge<3;charge++)
			{
				Ion<PeptideFragment> ion = new Ion<PeptideFragment>(pf,charge);
				System.out.println(""+pf.fragmentType+""+pf.ordinal+"^"+charge+" "+ion.mz());
			}
		}
		double mz=307.2;
		double tol=0.5;
		System.out.println(matchedIonsAsString(matchingIons(possibleYAndBIons(standardPeptideFromString(test),2),mz,tol)));
	}
	
	public static Peptide standardPeptideFromString(String sequence)
	{
		IAminoAcid[] aas = new IAminoAcid[sequence.length()]; 
		for (int i=0;i<sequence.length();i++)
		{
			aas[i]=StandardAminoAcid.fromChar(sequence.charAt(i));
		}
		return new Peptide(aas);		
	}
	
	public static List<Ion<PeptideFragment>> possibleYAndBIons(Peptide p, double maxCharge)
	{
		EPeptideFragment[] fragments = {EPeptideFragment.y,	EPeptideFragment.b};
		return possibleIons(p, fragments,maxCharge);
	}
	
	public static List<Ion<PeptideFragment>> possibleIons(Peptide p, EPeptideFragment[] fragments, double maxCharge)
	{
		ArrayList<Ion<PeptideFragment>> ions = new ArrayList<Ion<PeptideFragment>>();
		PeptideFragment[] frags = p.getFragments(fragments);
		for (PeptideFragment pf:frags)
		{
			for (int charge=1;charge<=maxCharge;charge++)
			{
				ions.add(new Ion<PeptideFragment>(pf,charge));
			}
		}
		return ions;
	}
	
	public static List<Ion<PeptideFragment>> matchingIons(List<Ion<PeptideFragment>> ions, double peakMz, double mzTolerance)
	{
		ArrayList<Ion<PeptideFragment>> matchedIons = new ArrayList<Ion<PeptideFragment>>();
		for (Ion<PeptideFragment> ion:ions)
		{
			if (Math.abs(ion.mz()-peakMz)<mzTolerance)
			{
				matchedIons.add(ion);
			}
		}
		return matchedIons;
	}
	
	public static String matchedIonsAsString(List<Ion<PeptideFragment>> ions)
	{
		String s = "";
		for (int i=0;i<ions.size();i++)
		{
			if (i>0) s+=",";
			s+=""+ions.get(i).molecule.fragmentType+""+ions.get(i).molecule.ordinal+"^"+ions.get(i).numExtraProtons;		
		}
		return s;
	}
	
}
