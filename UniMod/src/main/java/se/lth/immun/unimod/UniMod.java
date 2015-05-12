package se.lth.immun.unimod;

import se.lth.immun.chem.Peptide;
import se.lth.immun.chem.IAminoAcid;
import se.lth.immun.chem.IMolecule;
import se.lth.immun.chem.StandardAminoAcid;
import se.lth.immun.chem.ModifiedAminoAcid;

public class UniMod {

	
	public static Peptide parseUniModSequence(String seq) {
		IAminoAcid[] aas = new IAminoAcid[seq.length()];
		int is = 0;
		int iaa = 0;
		int sl = seq.length();
		while (is < sl) {
			StandardAminoAcid aa = StandardAminoAcid.fromChar(seq.charAt(is));
			if (aa == null)
				throw new IllegalArgumentException("Cannot parse amino acid '"+seq.charAt(is)+"' in peptide '"+seq+"'");
			is++;
			if (is < sl && seq.charAt(is) == '(') {
				int start = is + 8;
				is = start + 1;
				while (seq.charAt(is) != ')' && is < sl) is++;
				if (is == sl)
					throw new IllegalArgumentException("Cannot parse unimod modification '"+seq.substring(start-1)+"' in peptide '"+seq+"'");
				
				int acc = Integer.parseInt(seq.substring(start, is));
				UniModEntry ume = UniModEntry.fromUniModAcc(acc);
				if (ume == null)
					throw new IllegalArgumentException("Unknown unimod accession '"+acc+"' in peptide '"+seq+"'");
				
				is++;
				aas[iaa] = new ModifiedAminoAcid(aa, ume.modification);
				iaa++;
			} else {
				aas[iaa] = aa;
				iaa++;
			}		
		}
		
		IAminoAcid[] out = new IAminoAcid[iaa];
		System.arraycopy(aas, 0, out, 0, iaa);
		
		return new Peptide(out);
	}
	
	
	public static IAminoAcid[] parseFaultySequence(String seq) {
		IAminoAcid[] aas = new IAminoAcid[seq.length()];
		int is = 0;
		int iaa = 0;
		int sl = seq.length();
		while (is < sl) {
			StandardAminoAcid aa = StandardAminoAcid.fromChar(seq.charAt(is));
			//if (aa == null)
			//	throw new IllegalArgumentException("Cannot parse amino acid '"+seq.charAt(is)+"' in peptide '"+seq+"'");
			is++;
			if (is < sl && seq.charAt(is) == '(') {
				int start = is + 8;
				is = start + 1;
				while (seq.charAt(is) != ')' && is < sl) is++;
				IMolecule mod = null;
				if (is == sl) {
					//	throw new IllegalArgumentException("Cannot parse unimod modification '"+seq.substring(start-1)+"' in peptide '"+seq+"'");	
				} else {
					int acc = Integer.parseInt(seq.substring(start, is));
						UniModEntry ume = UniModEntry.fromUniModAcc(acc);
						if (ume == null) {
						//	throw new IllegalArgumentException("Unknown unimod accession '"+acc+"' in peptide '"+seq+"'");
						} else
							mod = ume.modification;
				}
				
				is++;
				aas[iaa] = new ModifiedAminoAcid(aa, mod);
				iaa++;
			} else {
				aas[iaa] = aa;
				iaa++;
			}		
		}
		
		IAminoAcid[] out = new IAminoAcid[iaa];
		System.arraycopy(aas, 0, out, 0, iaa);
		return out;
	}
}
