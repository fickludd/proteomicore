package se.lth.immun.signal;

/**
 * Class for storing different wavelet filters. Adapted from 
 * http://ubio.bioinfo.cnio.es/biotools/cpas/INSTALL/src/webapps/tools/modwt/Filter.java.<p/>
 * 
 * h = wavelet filter 
 * g = scaling filter
 * 
 * @author johant
 * 
 */
public class WaveletFilter {
	static public enum Type { HAAR, D4, D6, D8, LA8, LA16, C6 };
	final static double[] hhaar = new double[] { 
			0.7071067811865475,
			-0.7071067811865475 
		};
	final static double[] ghaar = new double[] { 
			0.7071067811865475,
			0.7071067811865475 
		};

	final static double[] hd4 = new double[] { 
			-0.1294095225512603,
			-0.2241438680420134, 
			 0.8365163037378077, 
			-0.4829629131445341 
		};
	final static double[] gd4 = new double[] { 
			 0.4829629131445341, 
			 0.8365163037378077,
			 0.2241438680420134, 
			-0.1294095225512603 
		};
	final static double[] hd6 = new double[] { 
			 0.0352262918857096, 
			 0.0854412738820267,
			-0.1350110200102546, 
			-0.4598775021184915, 
			 0.8068915093110928,
			-0.3326705529500827 
		};
	final static double[] gd6 = new double[] { 
			 0.3326705529500827, 
			 0.8068915093110928,
			 0.4598775021184915, 
			-0.1350110200102546, 
			-0.0854412738820267,
			 0.0352262918857096 
		};
	final static double[] hd8 = new double[] { 
			-0.0105974017850021,
			-0.0328830116666778, 
			 0.0308413818353661, 
			 0.1870348117179132,
			-0.0279837694166834, 
			-0.6308807679358788, 
			 0.7148465705484058,
			-0.2303778133074431 
		};
	final static double[] gd8 = new double[] { 
			 0.2303778133074431, 
			 0.7148465705484058,
			 0.6308807679358788, 
			-0.0279837694166834, 
			-0.1870348117179132,
			 0.0308413818353661, 
			 0.0328830116666778, 
			-0.0105974017850021
		};
	final static double[] hla8 = new double[] { 
			 0.03222310060407815,
			 0.01260396726226383, 
			-0.09921954357695636, 
			-0.29785779560560505,
			 0.80373875180538600, 
			-0.49761866763256290, 
			-0.02963552764596039,
			 0.07576571478935668 
		};
	final static double[] gla8 = new double[] { 
			-0.07576571478935668,
			-0.02963552764596039, 
			 0.49761866763256290, 
			 0.80373875180538600,
			 0.29785779560560505, 
			-0.09921954357695636, 
			-0.01260396726226383,
			 0.03222310060407815 
		};
	final static double[] hla16 = new double[] { 
			 0.0018899503329007,
			 0.0003029205145516, 
			-0.0149522583367926, 
			-0.0038087520140601,
			 0.0491371796734768, 
			 0.0272190299168137,
			-0.0519458381078751,
			-0.3644418948359564, 
			 0.7771857516997478,
			-0.4813596512592012,
			-0.0612733590679088, 
			 0.1432942383510542, 
			 0.0076074873252848,
			-0.0316950878103452, 
			-0.0005421323316355, 
			 0.0033824159513594 
		};
	final static double[] gla16 = new double[] { 
			-0.0033824159513594,
			-0.0005421323316355, 
			 0.0316950878103452, 
			 0.0076074873252848,
			-0.1432942383510542, 
			-0.0612733590679088, 
			 0.4813596512592012,
			 0.7771857516997478, 
			 0.3644418948359564, 
			-0.0519458381078751,
			-0.0272190299168137, 
			 0.0491371796734768, 
			 0.0038087520140601,
			-0.0149522583367926, 
			-0.0003029205145516, 
			 0.0018899503329007 
		};
	final static double[] hc6 = new double[] { 
			-0.072732275741189, 
			-0.337897670951159,
			 0.852572041642390, 
			-0.384864856538113, 
			-0.072732621341051,
			 0.015655728528985 
		};
	final static double[] gc6 = new double[] {
			-0.015655728528985, 
			-0.072732621341051,
			 0.384864856538113, 
			 0.852572041642390, 
			 0.337897670951159,
			-0.072732275741189
		};

	int length;
	double[] h;
	double[] g;

	
	
	protected WaveletFilter() {}

	
	
	/**
	 * Initiates the wavelet Filter and corresponding scaling Filter based
	 * on the input string 'choice'. <p/> 
	 * 
	 * @param choice	character string
	 * 					allowed values: "haar", "d4", "d6", "d8", "la8", "la16" or "c6" <p/> 
	 */
	public WaveletFilter(Type type) {
		switch (type) {
			case HAAR:
				length = 2;
				h = hhaar;
				g = ghaar;
				break;
			case D4:
				length = 4;
				h = hd4;
				g = gd4;
				break;
			case D6:
				length = 6;
				h = hd6;
				g = gd6;
				break;
			case D8:
				length = 8;
				h = hd8;
				g = gd8;
				break;
			case LA8:
				length = 8;
				h = hla8;
				g = gla8;
				break;
			case LA16:
				length = 16;
				h = hla16;
				g = gla16;
				break;
			case C6:
				length = 6;
				h = hc6;
				g = gc6;
				break;
			default:
				throw new IllegalArgumentException(
					"...unimplemented wavelet choice...");
		}
	}

	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Wavelet Filter (").append(length).append("):\n");
		sb.append("  h := \n");
		printdvec(sb, h);
		sb.append("  g := \n");
		printdvec(sb, g);
		return sb.toString();
	}

	
	
	private static void printdvec(StringBuffer sb, double[] v) {
		for (int i = 0; i < v.length; i++) {
			sb.append(v[i]).append(' ');
		}
		sb.append('\n');
	}

	
	
	/**
	 * This function converts the basic Haar wavelet filter (h0 = -0.7017, h1 =
	 * 0.7017) into a filter of length 'length*scale + 1' The Filter is re-normalized
	 * so that it's sum of squares equals 1. <p/> Input: f = haar wavelet Filter
	 * structure translate = integer <p/> Output: fills in h, g, and length for a
	 * wavelet Filter structure
	 */
	public WaveletFilter convert_haar(int scale) {
		WaveletFilter out = new WaveletFilter();
		double inv_sqrt_scale = 1.0 / Math.sqrt(scale);

		out.length 	= length * scale + 1;
		out.h 		= new double[length * scale + 1];
		out.g 		= new double[length * scale + 1];

		for (int l = 0; l < scale; l++) {
			out.h[l] = h[0] * inv_sqrt_scale;
			out.g[l] = g[0] * inv_sqrt_scale;
			out.h[l + scale] = h[1] * inv_sqrt_scale;
			out.g[l + scale] = g[1] * inv_sqrt_scale;
		}
		return (out);
	}
}
