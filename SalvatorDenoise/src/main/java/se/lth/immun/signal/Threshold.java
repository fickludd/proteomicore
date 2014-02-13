package se.lth.immun.signal;

/**
 * Helper class for applying thresholds to arrays of floats or doubles
 * @author johant
 *
 */
public class Threshold {

	/**
	 * Set all values in 'in' that have an absulute value lower than 'threshold' to 0.0.
	 * WARNING: modifies the input array!
	 * 
	 * @param in
	 * @param threshold		must be >= 0.0 otherwise in is unmodified
	 */
	public static void hard(double[] in, double threshold) {
			
		if (threshold <= 0.0) return;
		if (threshold == Double.MAX_VALUE)
			Arrays.zero(in);
		else {
			int N = in.length;
			for (int i = 0; i < N; i++) {
				double d = in[i];
				if (Math.abs(d) <= threshold)
					in[i] = 0.0;
			}
		}
	}
	public static void hard(float[] in, float threshold) {
			
		if (threshold <= 0.0) return;
		if (threshold == Double.MAX_VALUE)
			Arrays.zero(in);
		else {
			int N = in.length;
			for (int i = 0; i < N; i++) {
				double d = in[i];
				if (Math.abs(d) <= threshold)
					in[i] = 0.0f;
			}
		}
	}
	
	
	
	/**
	 * Set all values in 'in' that have an absolute value lower than 'threshold' to 0.0.
	 * Values larger than 'threshold' have their absolute value reduced by 'threshold'.
	 * WARNING: modifies the input array!
	 *  
	 * @param in
	 * @param threshold			must be >= 0.0 otherwise in is unmodified
	 */
	public static void soft(double[] in, double threshold) {

		if (threshold <= 0.0) return;
		if (threshold == Double.MAX_VALUE)
			Arrays.zero(in);
		else {
			int N = in.length;
			for (int i = 0; i < N; i++) {
				double d = in[i];
				in[i] = Math.abs(d) <= threshold ? 0.0 : (d < 0 ? d + threshold : d - threshold);
			}
		}
	}
	public static void soft(float[] in, float threshold) {

		if (threshold <= 0.0) return;
		if (threshold == Double.MAX_VALUE)
			Arrays.zero(in);
		else {
			int N = in.length;
			for (int i = 0; i < N; i++) {
				float d = in[i];
				in[i] = Math.abs(d) <= threshold ? 0.0f : (d < 0 ? d + threshold : d - threshold);
			}
		}
	}
}
