package se.lth.immun.signal;

/**
 * Class performing wavelet transformations. Inspired by class by
 * mbellew, Fred Hutchinson Cancer Research Center
 * 
 * http://ubio.bioinfo.cnio.es/biotools/cpas/INSTALL/src/webapps/tools/modwt/Transform.java
 * 
 * @author johant
 *
 */
public class Wavelet {
	
	static public enum Boundary 	{ PERIODIC, REFLECTIVE };
	
	

	/**
	 * Utility function performing DWT decomposition. mods 'out' if 
	 * given but not 'in'.
	 */
	private static void decompose(
			double[] 		in, 
			int 			length, 
			WaveletFilter 	f, 
			ITransform		t,
			DWaveletLevel[]	out,
			int				numLevels,
			boolean		keepAllSmooths
	) {
		DWaveletLevel curr = new DWaveletLevel(in, null, length, 0);
		DWaveletLevel next = DWaveletLevel.reuse(out[0], t.nextLevelLength(in.length));
		for (int k = 0; k < (numLevels-1); k++) {
			t.transform(curr, f, next);
			DWaveletLevel temp;
			if (keepAllSmooths)
				temp = DWaveletLevel.reuse(out[k+1], t.nextLevelLength(next.length));
			else
				temp = DWaveletLevel.reuseNoise(
							curr.smooth == in ? null : curr.smooth, 
							out[k+1], 
							t.nextLevelLength(next.length));
			curr 	= next;
			out[k] 	= next;
			next	= temp;
		}
		t.transform(curr, f, next);
		out[numLevels - 1] = next;
		
		if (!keepAllSmooths)
			for (int k = 0; k < (numLevels-1); k++) 
				out[k].smooth = null;
	}
	
	
	
	/**
	 * Peform the discrete wavelet transform (DWT) to a time-series 
	 * and obtain a specified number (K) of wavelet coefficients and 
	 * subsequent wavelet smooths. Reflection is used as the default 
	 * boundary condition.
	 * 
	 * @param in       		time-series (array of data)
	 * @param length       in length
	 * @param numLevels		number of details desired
	 * @param f        		wavelet filter to use
	 * @param boundary 		WaveletTransform.Boundary
	 * @param out     		arrays to be used to return results, may be null
	 * @return         	matrix of wavelet coefficients and smooth
	 */
	public static DWaveletLevel[] decompose(
			double[] 		in, 
			int 			length, 
			int 			numLevels, 
			WaveletFilter 	f, 
			ITransform		t,
			Boundary 		boundary, 
			DWaveletLevel[] out,
			boolean		keepAllSmooths
	) {
		double[] _start;
		int	_length;

		String str = t.inDataOk(in, length);
		if (str != null)
			throw new IllegalArgumentException(str);
		
		switch (boundary) {
			case REFLECTIVE:
				_start = Arrays.reflect(in, length, null);
				_length = _start.length;
				break;
				
			case PERIODIC:
				_start = in;
				_length = length;
				break;
				
			default: 
				throw new IllegalArgumentException("This shouldn't happen!");
		}
		
		if (out == null)
			out = new DWaveletLevel[numLevels];
		
		decompose(_start, _length, f, t, out, numLevels, keepAllSmooths);
		return out;
	}
	
	public static DWaveletLevel[] decompose(
			double[] 		in, 
			int 			length, 
			int 			numLevels, 
			WaveletFilter 	f, 
			ITransform		t,
			Boundary 		boundary, 
			DWaveletLevel[] out
	) {
		return decompose(in, length, numLevels, f, t, boundary, out, false);
	}
	
	public static DWaveletLevel[] decompose(
			double[] 		in, 
			int 			numLevels, 
			WaveletFilter 	f, 
			ITransform		t
	) {
		return decompose(in, in.length, numLevels, f, t, Boundary.PERIODIC, null, false);
	}
	
	
	
	/**
	 * 
	 */
	public static double[] compose(
			DWaveletLevel[]	in, 
			int				numLevels,
			WaveletFilter 	f, 
			ITransform		t,
			double[]		out
	) {
		int outLength 		= t.prevLevelLength(in[0].length);
		double[] outTemp	= new double[in[0].length];
		out = Arrays.realloc(out, outLength);
		
		DWaveletLevel curr = in[numLevels-1];
		DWaveletLevel next = in[numLevels-1];
		for (int k = numLevels-1; k > 0; k--) {
			next	= in[k-1].withSmooth(k % 2 == 1 ? outTemp : out);
			t.inverse(curr, f, next);
			curr 	= next;
		}
		
		t.inverse(next, f, new DWaveletLevel(out));
		return out;
	}
	
	
	
	/**
	 * Peform a multiresolution analysis using the DWT levels
	 * obtained from 'decompose.'  The inverse transform is applied
	 * to the isolated wavelet detail coefficients at each level.  
	 * The wavelet smooth coefficients from the original transform are 
	 * added to the K+1 column in order to preserve the additive decomposition.
	 *
	 * @param in    		output from 'decompose()'
	 * @param length		size of each array in 'in'
	 * @param numLevels    number of arrays in 'in'
	 * @param f      		wavelet filter
	 * @param type 			WaveletFilter.Type
	 * @param boundary 		WaveletFilter.Boundary
	 * @param out   		arrays to be used for return results, may be null
	 * @return 			array of K inversed wavelet details and 1 wavelet smooth
	 */
	public static double[][] multiresolution(
			DWaveletLevel[] in, 
			int 			numLevels, 
			WaveletFilter 	f,
			ITransform		t,
	        Boundary 		boundary, 
	        double[][] 		out
	) {
		if (out == null)
			out = new double[numLevels+1][];

		/*
		switch (boundary) {
			case REFLECTIVE:
				_length = 2 * in[0].length;
				break;
			case PERIODIC:
			default:
				_length = in[0].length;
				break;
		}
		*/
		DWaveletLevel[] temp = null;
		
		for (int k = 0; k < numLevels; k++) {
			temp 	= new DWaveletLevel[k+1];
			temp[k] = new DWaveletLevel(null, in[k].noise, in[k].length, k+1);
			out[k] 	= Arrays.realloc(out[k], in[k].length * 2); 
			compose(temp, k+1, f, t, out[k]);
		}
		
		int k 		= numLevels - 1;
		temp[k] 	= new DWaveletLevel(in[k].smooth, null, in[k].length, k+1);
		out[k+1] 	= Arrays.realloc(out[k+1], in[k].length * 2); 
		compose(temp, k+1, f, t, out[k+1]);

		return out;
	}
	
	
	
	/**
	 * Get the smooths at all the levels. 
	 *
	 * @param in    		output from 'decompose()'
	 * @param length		size of each array in 'in'
	 * @param numLevels    number of arrays in 'in'
	 * @param f      		wavelet filter
	 * @param type 			WaveletFilter.Type
	 * @param boundary 		WaveletFilter.Boundary
	 * @param out   		arrays to be used for return results, may be null
	 * @return 			array of K inversed wavelet details and 1 wavelet smooth
	 */
	public static double[][] smooths(
			DWaveletLevel[] in, 
			int 			numLevels, 
			WaveletFilter 	f,
			ITransform		t,
	        Boundary 		boundary, 
	        double[][] 		out
	) {
		if (out == null)
			out = new double[numLevels][];
		
		int outLength 		= t.prevLevelLength(in[0].length);
		double[] smoothTemp1	= new double[in[0].length];
		double[] smoothTemp2	= new double[in[0].length];
		
		DWaveletLevel curr = in[numLevels-1];
		DWaveletLevel next = in[numLevels-1];
		DWaveletLevel[] temp = new DWaveletLevel[numLevels];
		for (int j = 0; j < numLevels-1; j++) 
			temp[j] = in[j].zero();
		
		for (int k = numLevels-1; k > 0; k--) {
			next	= in[k-1].withSmooth(k % 2 == 1 ? smoothTemp1 : smoothTemp2);
			t.inverse(curr, f, next);
			temp[k] = curr.zeroNoise();
			out[k] = compose(temp, k+1, f, t, new double[outLength]);
			
			curr 	= next;
		}
		temp[0] = next.zeroNoise();
		out[0] = compose(temp, 1, f, t, new double[outLength]);
		return out;
	}
	
	
	

	private static double[] threshHard(double[] in, int l, double t) {
		if (in == null) return null;
		double[] ret = new double[l];
		for (int i = 0; i < l; i++)
			ret[i] = in[i] < t ? 0.0 : in[i];
		return ret;
	}
	public static double[] denoise(
			DWaveletLevel[] in, 
			int 			numLevels, 
			WaveletFilter 	f,
			ITransform		t,
			double			threshold,
			double[] 		out
	) {
		if (out == null)
			out = new double[t.prevLevelLength(in[0].length)];
		
		DWaveletLevel[] temp = new DWaveletLevel[numLevels];
		for (int i = 0; i < numLevels; i++) {
			temp[i] = new DWaveletLevel(
					threshHard(in[i].smooth, in[i].length, threshold),
					threshHard(in[i].noise, in[i].length, threshold),
					in[i].length,
					in[i].level
				);
		}
		
		compose(temp, numLevels, f, t, out);
		return out;
	}
}
