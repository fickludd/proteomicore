package se.lth.immun.signal;

public class Arrays {

	public static void copy(double[] src, double[] dst) {
		assert src.length == dst.length;
		System.arraycopy(src, 0, dst, 0, src.length);
	}

	public static void copy(float[] src, float[] dst) {
		assert src.length == dst.length;
		System.arraycopy(src, 0, dst, 0, src.length);
	}

	

	public static void zero(double[] zero) {
		java.util.Arrays.fill(zero, 0, zero.length, 0.0);
	}

	public static void zero(float[] zero) {
		java.util.Arrays.fill(zero, 0, zero.length, 0.0F);
	}
	
	
	
	/**
	 * Helper to facilitate reusing arrays and hopefully reduce allocations
	 *
	 * @param array		a array to possibly reuse
	 * @param length	required length of the return array
	 * @return			array of length 'legnth'. either reused of new.
	 */
	public static double[] realloc(double[] array, int length) {
		if (array == null || array.length < length)
			array = new double[length];
		boolean debug = false;
		assert true == (debug = true);
		if (debug)
			java.util.Arrays.fill(array, Double.NaN);
		return array;
	}

	public static float[] realloc(float[] array, int length) {
		if (array == null || array.length < length)
			array = new float[length];
		boolean debug = false;
		assert true == (debug = true);
		if (debug)
			java.util.Arrays.fill(array, Float.NaN);
		return array;
	}
	
	
	/**
	 * The functions for computing wavelet transforms assume periodic
	 * boundary conditions, regardless of the data's true nature.  By
	 * adding a 'backwards' version of the data to the end of the current
	 * data vector, we are essentially reflecting the data.  This allows
	 * the periodic methods to work properly.
	 */
	public static double[] reflect(double[] in, int N,  double[] out) {
		if (out == null)
			out = new double[2 * N];
		
		for (int t = 0; t < N; t++)
			out[t] = in[t];
		for (int t = 0; t < N; t++)
			out[N + t] = in[N - 1 - t];
		
		return out;
	}
	
	public static float[] reflect(float[] in, int N,  float[] out) {
		if (out == null)
			out = new float[2 * N];
		
		for (int t = 0; t < N; t++)
			out[t] = in[t];
		for (int t = 0; t < N; t++)
			out[N + t] = in[N - 1 - t];
		
		return out;
	}
}
