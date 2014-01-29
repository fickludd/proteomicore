package se.lth.immun.chem;

public enum EPeptideFragment {
	a('a'), b('b'), c('c'), x('x'), y('y'), z('z');
	
	public final char type;
	
	private EPeptideFragment(char type) {
		this.type = type;
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
