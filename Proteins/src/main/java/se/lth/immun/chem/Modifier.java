package se.lth.immun.chem;

import java.util.ArrayList;
import java.util.Map;

public class Modifier {
	
	static class Mod implements IModifier {
		
		IMolecule m;
		String str;
		
		public Mod(IMolecule m, String str) {
			this.m = m;
			this.str = str;
		}
		
		public boolean isTarget(IAminoAcid left, IAminoAcid x, IAminoAcid right) { return true; }
		public IMolecule modification() { return m; }
		public String modStr() { return str; }
	}
	
	static class NtermMod extends Mod {
		
		char xc;
		char rc;
		
		public NtermMod(IMolecule m, String str, char xc, char rc) {
			super(m, str);
			this.xc = xc;
			this.rc = rc;
		}
		public boolean isTarget(IAminoAcid left, IAminoAcid x, IAminoAcid right) { 
			return left == null && okAA(x, xc) && okAA(right, rc); 
		}
	}
	
	static class CtermMod extends Mod {
		
		char xc;
		char lc;
		
		public CtermMod(IMolecule m, String str, char lc, char xc) {
			super(m, str);
			this.xc = xc;
			this.lc = lc;
		}
		public boolean isTarget(IAminoAcid left, IAminoAcid x, IAminoAcid right) { 
			return okAA(left, lc) && okAA(x, xc) && right == null; 
		}
	}
	
	static class InnerMod extends Mod {

		char lc;
		char xc;
		char rc;
		
		public InnerMod(IMolecule m, String str, char lc, char xc, char rc) {
			super(m, str);
			this.lc = lc;
			this.xc = xc;
			this.rc = rc;
		}
		public boolean isTarget(IAminoAcid left, IAminoAcid x, IAminoAcid right) { 
			return okAA(left, lc) && okAA(x, xc) && okAA(right, rc); 
		}
	}

	static Element[] SILAC_R_ES 	= {Element.N, Element.N15, Element.C, Element.C13};
	static int[] SILAC_R_CS 		= {-4, 4, -6, 6};
	static Mod SILAC_R 				= new CtermMod(
										new Molecule(SILAC_R_ES, SILAC_R_CS), 
										".R| += C(-6) 13C(6) N(-4) 15N(4)", 
										'.', 
										'R');
	static Element[] SILAC_K_ES 	= {Element.N, Element.N15, Element.C, Element.C13};
	static int[] SILAC_K_CS 		= {-2, 2, -6, 6};
	static Mod SILAC_K 				= new CtermMod(
										new Molecule(SILAC_K_ES, SILAC_K_CS), 
										".R| += C(-6) 13C(6) N(-2) 15N(2)", 
										'.', 
										'K');
	
	
	
	public static IMolecule parseMolecule(String str) {
		return parseMolecule(str, null);
	}
	
	public static IMolecule parseMolecule(String str, Map<String, ElementComposition> dict) {
		String[] elementStrs = str.split(" ");
		int n 				= elementStrs.length;
		int[] counts 		= new int[n];
		Element[] elements 	= new Element[n];
		int ie = 0;
		
		ElementComposition dictBuild = null;
		
		for (int i = 0; i < n; i++) {
			String eStr = elementStrs[i].trim();
			if (eStr.matches("(\\d*[a-zA-Z]+)\\((-?\\d+)\\)")) {
				String[] x = eStr.split("\\(");
				Element e = Element.fromString(x[0]);
				int count = Integer.parseInt(x[1].substring(0, x[1].length() - 1));
				
				if (e != null) {
					elements[ie] = e;
					counts[ie] = count;
					ie++;
				} else if (dict != null && dict.containsKey(x[0])) {
					if (dictBuild == null) 
						dictBuild = dict.get(x[0]).multiply(count);
					else
						dictBuild = dictBuild.join(dict.get(x[0]).multiply(count));
				} else
					throw new IllegalArgumentException("Unknown counted element '"+x[0]+"'");
				
			} else if (dict != null && dict.containsKey(eStr)) {
				if (dictBuild == null) 
					dictBuild = dict.get(eStr);
				else
					dictBuild = dictBuild.join(dict.get(eStr));
			} else if (eStr.matches("\\d*[a-zA-Z]+")) {
				Element e = Element.fromString(eStr);
				if (e == null)
					throw new IllegalArgumentException("Unknown single element '"+eStr+"'");
				elements[ie] = e;
				counts[ie] = 1;
				ie++;
			} else
				throw new IllegalArgumentException("Element '"+eStr+"' not parsable");
			
		}
		
		if (dictBuild == null)
			return new Molecule(take(ie, elements), take(ie, counts));
		else
			return new Molecule(dictBuild.join(new ElementComposition(take(ie, elements), take(ie, counts))));
	}

	private static int[] take(int n, int[] in) {
		int[] out = new int[n];
		System.arraycopy(in, 0, out, 0, n);
		return out;
	}
	private static Element[] take(int n, Element[] in) {
		Element[] out = new Element[n];
		System.arraycopy(in, 0, out, 0, n);
		return out;
	}
	
	
	/**
	 * IModifier are specified as XYZ += chem_formula,
	 * where X, Y and Z are standard amino acids (capital letter) or '.' or '|'.
	 * '.' is interpreted as any amino acid including terminal, and '|' as terminal
	 * the Y amino acid is then modified by the chem_formula. 
	 *
	 * Example, SILAC heavy Arginine:
	 * 		.R| += C(-6) 13C(6) N(-4) 15N(4)
	 * 
	 * Special CASE-INSENSITIVE keywords are
	 * 	SilacR
	 * 	SilacK
	 * 	Silac
	 * 
	 * @param str	the String to parse for one or more IModifiers 
	 * @return 	list of modifier objects, that can be used to modify a Peptide
	 */
	public static ArrayList<IModifier> fromString(String str) {
		System.out.println("parsing mod: "+str);

		ArrayList<IModifier> ret = new ArrayList<IModifier>();
		if (str.toLowerCase().equals("silacr")) 
			ret.add(SILAC_R);
		else if (str.toLowerCase().equals("silack")) 
			ret.add(SILAC_K);
		else if (str.toLowerCase().equals("silac")) {
			ret.add(SILAC_K);
			ret.add(SILAC_R);
		}
		if (!ret.isEmpty()) 
			return ret;
		
		if (!str.contains("+="))
			throw new IllegalArgumentException("Cannot parse '"+str+"' as mod");
		
		String[] nv = str.split("\\+=");
		String name = nv[0].trim();
		String value = nv[1].trim();

		if (name.length() != 3)
			throw new IllegalArgumentException("Cannot parse '"+str+"' as mod");
		
		IMolecule mol = parseMolecule(value);

		char lc = name.charAt(0);
		char xc = name.charAt(1);
		char rc = name.charAt(2);
		
		if (name.equals("...")) 
			ret.add(new Mod(mol, str.trim()));
		else if (lc == '|')
			ret.add(new NtermMod(mol, str.trim(), xc, rc));
		else if (rc == '|')
			ret.add(new CtermMod(mol, str.trim(), lc, xc));
		else 
			ret.add(new InnerMod(mol, str.trim(), lc, xc, rc));
		return ret;
	}

	private static boolean okAA(IAminoAcid x, char xStr) {
		return xStr == '.' || StandardAminoAcid.fromChar(xStr) == x;
	}
	
	
	
	/**
	 * Creates a modified version of the Peptide p according to the provided IModifiers
	 * @param p
	 * @param modifiers
	 * @return
	 */
	public static Peptide modify(Peptide p, IModifier[] modifiers) {
		IAminoAcid[] aas = p.aminoAcids;
		IAminoAcid l, m, r;
		for (IModifier mod : modifiers) {
			IAminoAcid[] next = new IAminoAcid[aas.length];
			for (int i = 0; i < next.length; i++) {
				if (i == 0) l = null; else l = aas[i-1];
				m = aas[i];
				if (i == aas.length - 1) r = null; else r = aas[i+1];

				if (mod.isTarget(l, m, r)) 	next[i] = new ModifiedAminoAcid(aas[i], mod.modification());
				else 						next[i] = aas[i];
			}
			aas = next;
		}
		return new Peptide(aas);
	}
}
