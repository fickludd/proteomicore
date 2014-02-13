package se.lth.immun.chem;

import se.lth.immun.chem.Element;

/**
 * Representation of a single residual mass amino acid. The COOH-group is added by the peptide class.
 * @author johant
 *
 */
public enum StandardAminoAcid implements IAminoAcid {

	A ('A', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O				}, new int[]{3, 5, 	1, 1})), 
	R ('R', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O				}, new int[]{6, 12, 4, 1})), 
	N ('N', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O				}, new int[]{4, 6, 	2, 2})), 
	D ('D', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O				}, new int[]{4, 5, 	1, 3})), 
	C ('C', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O,Element.S	}, new int[]{3, 5, 	1, 1, 1})), 
	Q ('Q', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O				}, new int[]{5, 8, 2, 2})), 
	E ('E', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O				}, new int[]{5, 7, 1, 3})), 
	G ('G', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O				}, new int[]{2, 3, 1, 1})), 
	H ('H', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O				}, new int[]{6, 7, 3, 1})), 
	I ('I', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O				}, new int[]{6, 11, 1, 1})), 
	L ('L', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O				}, new int[]{6, 11, 1, 1})), 
	K ('K', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O				}, new int[]{6, 12, 2, 1})), 
	M ('M', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O,Element.S	}, new int[]{5, 9, 1, 1, 1})), 
	F ('F', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O				}, new int[]{9, 9, 1, 1})), 
	P ('P', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O				}, new int[]{5, 7, 1, 1})), 
	S ('S', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O				}, new int[]{3, 5, 1, 2})), 
	T ('T', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O				}, new int[]{4, 7, 1, 2})), 
	W ('W', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O				}, new int[]{11, 10, 2, 1})), 
	Y ('Y', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O				}, new int[]{9, 9, 1, 2})), 
	V ('V', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O				}, new int[]{5, 9, 1, 1})), 
	X ('X', new ElementComposition(new Element[]{Element.C,Element.H,Element.N,Element.O				}, new int[]{6, 11, 1, 1}));
	
	final ElementComposition composition;
	public final char	letter;
	final IsotopeDistribution _dist;

	private StandardAminoAcid(char letter, ElementComposition composition) {
		this.letter = letter;
		this.composition = composition;
		IsotopeDistribution dist = new IsotopeDistribution();
		for (int i = 0; i < composition.elements.length; i++) {
			dist.add(composition.elements[i].isotopeDistribution.mult(composition.counts[i]));
		}
		this._dist = dist;
	}

	public ElementComposition getComposition() { return composition; }
	
	public String toString() { return ""+letter; }
	
	public static StandardAminoAcid fromChar(char c) {
		switch (c) {
			case 'A': return A;
			case 'R': return R;
			case 'N': return N;
			case 'D': return D;
			case 'C': return C;
			case 'Q': return Q;
			case 'E': return E;
			case 'G': return G;
			case 'H': return H;
			case 'I': return I;
			case 'L': return L;
			case 'K': return K;
			case 'M': return M;
			case 'F': return F;
			case 'P': return P;
			case 'S': return S;
			case 'T': return T;
			case 'W': return W; 
			case 'Y': return Y; 
			case 'V': return V; 
			case 'X': return X; 
			default: return null;
		}
	}

	@Override
	public double monoisotopicMass() {
		return composition.monoisotopicMass();
	}
	
	public IsotopeDistribution getIsotopeDistribution() {
		return _dist;
	}
}
