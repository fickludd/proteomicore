package se.lth.immun.signal;

/**
 * Adapted from pseudocode by Donald B. Percival, 
 * http://dx.doi.org/10.1080/01621459.1997.10474042
 * 
 * @author johant
 *
 */
public class MODWT implements ITransform {

	final static double INV_SQRT_2 = 1.0 / Math.sqrt(2.0);
	static final int pow2(int k) { return 1 << k; }

	private static int wrap(int i, int length) {
		if (i >= length) 	return i - length;
		if (i < 0) 			return i + length;
		return i;
	}
	
	/**
	 * Compute the maximal overlap discrete wavelet transform (MODWT).
	 * This method uses the pyramid algorithm and was adapted from
	 * pseudo-code written by D. B. Percival. Output array length is constant.
	 *
	 * @param in  			array of wavelet smooths (data if k=1)
	 * @param length    	length of in
	 * @param level    		level (1, 2, ...)
	 * @param f    			wavelet filter (e.g., Haar, D(4), LA(8), ...)
	 * @param outNoise 		output array of wavelet coefficients
	 * @param outSmooth 	output array of wavelet smooths
	 */
	public void transform(
			DWaveletLevel 	in, 
			WaveletFilter 	f, 
			DWaveletLevel 	out
	) {
		out.level 	= in.level + 1;
		out.length 	= in.length;
		
		int il = in.length;
		int fl = f.length;
		double[] inSmooth 	= in.smooth;
		double[] noise		= out.noise;
		double[] smooth 	= out.smooth;
		int pow2_k 			= pow2(in.level);
		double[] ht = new double[fl];
		double[] gt = new double[fl];

		for (int l = 0; l < fl; l++) {
			ht[l] = f.h[l] * INV_SQRT_2;
			gt[l] = f.g[l] * INV_SQRT_2;
		}

		for (int t = 0; t < il; t++) {
			int j = t;
			noise[t] 	= 0.0;
			smooth[t] 	= 0.0;
			for (int fi = 0; fi < fl; fi++) {
				noise[t] 	+= ht[fi] * inSmooth[j];
				smooth[t] 	+= gt[fi] * inSmooth[j];
				j = wrap(j - pow2_k, il);
			}
		}
	}
	
	
	
	/**
	 * Compute the inverse maximal overlap discrete wavelet transform (IMODWT)
	 * via the pyramid algorithm.  Adapted from pseudo-code written by 
	 * D. B. Percival.
	 * 
	 * @param inNoise  		array of wavelet coefficients
	 * @param inSmooth  	array of wavelet smooths
	 * @param length    	length of inNoise and inSmooth
	 * @param level    		level (1, 2, ...)
	 * @param f    			wavelet filter
	 * @param out 			out vector of wavelet smooths
	 */

	public void inverse(
			DWaveletLevel in, 
			WaveletFilter f, 
			DWaveletLevel out
	) {
		int il = in.length;
		int fl = f.length;
		double[] ht = new double[fl];
		double[] gt = new double[fl];
		double[] smooth 	= in.smooth;
		double[] noise 		= in.noise;
		double[] outSmooth = out.smooth;
		int pow2_k 			= pow2(in.level-1);

		for (int l = 0; l < fl; l++) {
			ht[l] = f.h[l] * INV_SQRT_2;
			gt[l] = f.g[l] * INV_SQRT_2;
		}

		for (int t = 0; t < il; t++) {
			int j = t;
			outSmooth[t] = 0.0;
			for (int fi = 0; fi < fl; fi++) {
				outSmooth[t] += ht[fi] * noise[j] + gt[fi] * smooth[j];
				j = wrap(j + pow2_k, il);
			}
		}
		out.level 	= in.level - 1;
		out.length 	= in.length;
	}
	
	
	
	public String inDataOk(
			double[] 		in, 
			int 			length
	) {
		if (in == null || length <= 0) return "no in data!";
		return null;
	}
	
	

	public int nextLevelLength(int length) { return length; }
	public int prevLevelLength(int length) { return length; }
}
