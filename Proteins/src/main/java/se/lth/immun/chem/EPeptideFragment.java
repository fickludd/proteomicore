package se.lth.immun.chem;

public enum EPeptideFragment {
	a('a'), b('b'), c('c'), x('x'), y('y'), z('z');
	
	public final char type;
	public static final double A_MASS_DIFF = Element.O.monoisotopicWeight + Element.C.monoisotopicWeight;
	public static final double X_MASS_DIFF = Element.O.monoisotopicWeight + Element.C.monoisotopicWeight - 2*Element.H.monoisotopicWeight;
	public static final double CZ_MASS_DIFF = Element.N.monoisotopicWeight + 3*Element.H.monoisotopicWeight;
	
	private EPeptideFragment(char type) {
		this.type = type;
	}
	
	public boolean isNTerm() {
		return type == 'a' || type == 'b' || type == 'c';
	}
	
	public static EPeptideFragment fromChar(char ch) {
		switch (ch) {
			case 'a': return a;
			case 'b': return b;
			case 'c': return c;
			case 'x': return x;
			case 'y': return y;
			case 'z': return z;
			default: return null;
		}
	}
}
