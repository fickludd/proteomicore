package se.lth.immun.signal;

public class DWT implements ITransform {

	/**
	 * Compute the discrete wavelet transform (DWT).  This method uses the
	 * pyramid algorithm and was adapted from pseudo-code written by
	 * D. B. Percival.  Periodic boundary conditions are assumed. Output
	 * array length is halved.
	 *
	 * @param in  		array of wavelet smooths (data if first iteration)
	 * @param length 	length of in
	 * @param f    		wavelet filter (e.g., Haar, D(4), LA(8), ...)
	 * @param Wout 		output array of wavelet coefficients
	 * @param Vout 		output array of wavelet smooths
	 */
	public void transform(
			DWaveletLevel 	in, 
			WaveletFilter 	f, 
			DWaveletLevel	out
	) {
		double[] inSmooth = in.smooth;
		int length = in.length;
		double[] n = out.noise;
		double[] s = out.smooth;
		for (int t = 0; t < out.length; t++) {
			n[t] 	= 0.0;
			s[t] 	= 0.0;
			for (int l = 0, k = 2 * t + 1; l < f.length; l++) {
				n[t] 	+= f.h[l] * inSmooth[k];
				s[t] 	+= f.g[l] * inSmooth[k];
				k = k == 0 ? length - 1 : k - 1;
			}
		}
		out.length = length / 2;
		out.level = in.level + 1;
	}
	
	
	
	/**
	 * Compute the inverse discrete wavelet transform (IDWT) via the pyramid 
	 * algorithm.  This code was adapted from pseudo-code written by D. B. Percival.  
	 * Periodic boundary conditions are assumed.
	 *
	 * @param inNoise 		array of wavelet coefficients
	 * @param inSmooth 		array of wavelet smooths
	 * @param length   		length of inNoise and inSmooth
	 * @param f   			wavelet filter (e.g., Haar, D(4), LA(8), ...)
	 * @param out 			output array of reconstructed wavelet smooths (eventually the data)
	 */
	public void inverse(
			DWaveletLevel 	in, 
			WaveletFilter 	f, 
			DWaveletLevel 	out
	) {
		double[] noise 		= in.noise;
		double[] smooth 	= in.smooth;
		double[] outSmooth 	= out.smooth;
		int length = in.length;
		for (int t = 0, m = 0, n = 1; t < length; t++) {
			int u = t;
			int i = 1;
			int j = 0;
			outSmooth[m] = outSmooth[n] = 0.0;
			for (int l = 0; l < f.length / 2; l++) {
				outSmooth[m] += f.h[i] * noise[u] + f.g[i] * smooth[u];
				outSmooth[n] += f.h[j] * noise[u] + f.g[j] * smooth[u];
				u = u + 1 >= length ? 0 : u + 1;
				i += 2;
				j += 2;
			}
			m += 2;
			n += 2;
		}
		out.length = length * 2;
		out.level = in.level - 1;
	}
	
	
	
	

	public String inDataOk(
			double[] 		in, 
			int 			length
	) {
		if (in == null || length <= 0) return "no in data!";
		int l = length;
		int t = 1;
		while (t < l) t *= 2;
		if (t != l)
			 return "Data must have dyadic length for DWT, got length '"+l+"'";
		return null;
	}
	
	
	

	public int nextLevelLength(int length) { return length / 2; }
	public int prevLevelLength(int length) { return length * 2; }
}
