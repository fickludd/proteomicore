package se.lth.immun.signal;

import static org.junit.Assert.*;

import org.junit.Test;

public class MODWTTest {

	double[] x = {
			20.7742, 30.1246, 47.0923, 23.0488, 84.4309,
			19.4764, 22.5922, 17.0708, 22.7664, 43.5699, 
			31.1102, 92.3381, 43.0207, 18.4816, 90.4881, 
			97.9748, 43.8870, 11.1119, 25.8065, 40.8722
			};
	double[] x2 = {
			20.7742, 30.1246, 47.0923, 23.0488, 84.4309,
			19.4764, 22.5922, 17.0708, 22.7664, 43.5699, 
			31.1102, 92.3381, 43.0207, 18.4816, 90.4881, 
			97.9748, 43.8870, 11.1119, 25.8065, 40.8722,
			19.4764, 22.5922, 17.0708, 22.7664, 43.5699, 
			31.1102, 92.3381, 43.0207, 18.4816, 90.4881,
			43.8870, 11.1119
			};
	WaveletFilter f = new WaveletFilter(WaveletFilter.Type.HAAR);
	MODWT modwt = new MODWT();
	
	
	
	@Test
	public void modwt() {
		DWaveletLevel in 	= new DWaveletLevel(x);
		DWaveletLevel out 	= new DWaveletLevel(new double[20], new double[20], 20, 0);
		
		modwt.transform(in, f, out);
		assertEquals(0, in.level);
		assertEquals(20, in.length);
		for (int i = 0; i < 20; i++) 
			assertEquals(x[i], in.smooth[i], Double.MIN_VALUE);
		assertEquals(1, out.level);
		assertEquals(20, out.length);

		for (int i = 0; i < 20; i++) {
			assertTrue(out.smooth[i] != 0.0);
		}

		
		double[] ownW = new double[20];
		double[] ownV = new double[20];
		double[] Vout3 = new double[20];
		modwt.transform(
				new DWaveletLevel(x, null, 20, 0), 
				f, 
				new DWaveletLevel(ownV, ownW, 20, 1)
			);
		salvo_imodwt(ownW, ownV, 20, 1, f, Vout3);
		for (int i = 0; i < 20; i++) 
			assertEquals(x[i], Vout3[i], 0.001);
	}
	

	
	@Test
	public void imodwt() {
		DWaveletLevel in 	= new DWaveletLevel(x);
		DWaveletLevel out 	= new DWaveletLevel(new double[20], new double[20], 20, 0);
		DWaveletLevel out2 	= new DWaveletLevel(new double[20]);

		modwt.transform(in, f, out);
		modwt.inverse(out, f, out2);
		assertEquals(1, out.level);
		assertEquals(20, out.length);
		assertEquals(0, out2.level);
		assertEquals(20, out2.length);
		
		for (int i = 0; i < 20; i++) 
			assertEquals(x[i], out2.smooth[i], 0.001);
		
		
		

		double[] Wout = new double[20];
		double[] Vout = new double[20];
		double[] ownOut = new double[20];
		salvo_modwt(x, 20, 1, f, Wout, Vout);
		modwt.inverse(
				new DWaveletLevel(Vout, Wout, 20, 1), 
				f, 
				new DWaveletLevel(ownOut, null, 20, 0)
			);
		for (int i = 0; i < 20; i++) 
			assertEquals(x[i], ownOut[i], 0.001);
	}
	
	

	@Test
	public void decomposePeriodic() {
		DWaveletLevel[] out = Wavelet.decompose(x2, 32, 3, f, modwt, 
									Wavelet.Boundary.PERIODIC, null);
		assertEquals(3, out.length);
		
		assertEquals(32, out[0].length);
		assertEquals(1, out[0].level);
		assertNotNull(out[0].noise);
		assertNull(out[0].smooth);
		
		assertEquals(32, out[1].length);
		assertEquals(2, out[1].level);
		assertNotNull(out[1].noise);
		assertNull(out[1].smooth);
		
		assertEquals(32, out[2].length);
		assertEquals(3, out[2].level);
		assertNotNull(out[2].noise);
		assertNotNull(out[2].smooth);
	}
	
	

	@Test
	public void decomposeReflective() {
		DWaveletLevel[] out = Wavelet.decompose(x2, 32, 3, f, modwt, 
								Wavelet.Boundary.REFLECTIVE, null);
		assertEquals(3, out.length);
		
		assertEquals(64, out[0].length);
		assertEquals(1, out[0].level);
		assertNotNull(out[0].noise);
		assertNull(out[0].smooth);
		
		assertEquals(64, out[1].length);
		assertEquals(2, out[1].level);
		assertNotNull(out[1].noise);
		assertNull(out[1].smooth);
		
		assertEquals(64, out[2].length);
		assertEquals(3, out[2].level);
		assertNotNull(out[2].noise);
		assertNotNull(out[2].smooth);
	}
	
	

	@Test
	public void decomposeKeepAll() {
		DWaveletLevel[] out = Wavelet.decompose(x2, 32, 3, f, modwt, 
									Wavelet.Boundary.PERIODIC, null, true);
		assertEquals(3, out.length);
		
		assertEquals(32, out[0].length);
		assertEquals(1, out[0].level);
		assertNotNull(out[0].noise);
		assertEquals(32, out[0].noise.length);
		assertNotNull(out[0].smooth);
		assertEquals(32, out[0].noise.length);
		for (int i = 0; i < 32; i++) 
			assertTrue(0.0 != out[0].smooth[i]);
		
		assertEquals(32, out[1].length);
		assertEquals(2, out[1].level);
		assertNotNull(out[1].noise);
		assertEquals(32, out[1].noise.length);
		assertNotNull(out[1].smooth);
		assertEquals(32, out[1].noise.length);
		for (int i = 0; i < 32; i++) 
			assertTrue(0.0 != out[1].smooth[i]);
		
		assertEquals(32, out[2].length);
		assertEquals(3, out[2].level);
		assertNotNull(out[2].noise);
		assertEquals(32, out[2].noise.length);
		assertNotNull(out[2].smooth);
		assertEquals(32, out[2].noise.length);
		for (int i = 0; i < 32; i++) 
			assertTrue(0.0 != out[2].smooth[i]);

		assertTrue(out[0].smooth != out[1].smooth);
		assertTrue(out[0].smooth != out[2].smooth);
		assertTrue(out[1].smooth != out[2].smooth);
		
		for (int l = 1; l < 4; l++) {
			double[] out2 = Wavelet.compose(out, l, f, modwt, null);
			for (int i = 0; i < 32; i++) 
				assertEquals(x2[i], out2[i], 0.001);
		}
	}
	
	

	@Test
	public void composePeriodic() {
		int numLevels = 3;
		DWaveletLevel[] out = Wavelet.decompose(x2, 32, numLevels, f, modwt, 
											Wavelet.Boundary.PERIODIC, null);
		double[] out2 = Wavelet.compose(out, numLevels, f, modwt, null);

		for (int i = 0; i < 32; i++) 
			assertEquals(x2[i], out2[i], 0.001);
	}
	
	

	@Test
	public void composeReflective() {
		int numLevels = 3;
		DWaveletLevel[] out = Wavelet.decompose(x2, 32, numLevels, f, modwt, 
										Wavelet.Boundary.REFLECTIVE, null);
		double[] out2 = Wavelet.compose(out, numLevels, f, modwt, null);

		for (int i = 0; i < 32; i++) 
			assertEquals(x2[i], out2[i], 0.001);
	}
	
	
	
	@Test
	public void salvo_imodwt() {
		double[] Wout = new double[20];
		double[] Vout = new double[20];
		double[] Vout2 = new double[20];

		salvo_modwt(x, 20, 1, f, Wout, Vout);
		salvo_imodwt(Wout, Vout, 20, 1, f, Vout2);
		for (int i = 0; i < 20; i++) 
			assertEquals(x[i], Vout2[i], 0.001);
	}

	
	
	

	/**
	 * Compute the maximal overlap discrete wavelet Transform (MODWT).
	 * This method uses the pyramid algorithm and was adapted from
	 * pseudo-code written by D. B. Percival.
	 *
	 * @param Vin  vector of wavelet smooths (data if k=1)
	 * @param N    length of Vin
	 * @param k    iteration (1, 2, ...)
	 * @param f    wavelet Filter structure (e.g., Haar, D(4), LA(8), ...)
	 * @param Wout OUT vector of wavelet coefficients
	 * @param Vout OUT vector of wavelet smooths
	 */
	public static void salvo_modwt(double[] Vin, int N, int k, 
					WaveletFilter f, double[] Wout, double[] Vout) {
		double[] ht = new double[f.length];
		double[] gt = new double[f.length];
		int pow2_k = pow2(k - 1);

		for (int l = 0; l < f.length; l++)
			{
			ht[l] = f.h[l] * INV_SQRT_2;
			gt[l] = f.g[l] * INV_SQRT_2;
			}

		for (int t = 0; t < N; t++)
			{
			int j = t;
			Wout[t] = Vout[t] = 0.0;
			for (int l = 0; l < f.length; l++)
				{
				Wout[t] += ht[l] * Vin[j];
				Vout[t] += gt[l] * Vin[j];
				j = wrap(j - pow2_k, N);
				}
			}
		}
	

	/**
	 * Compute the inverse MODWT via the pyramid algorithm.  Adapted from
	 * pseudo-code written by D. B. Percival.
	 * @param Win  vector of wavelet coefficients
	 * @param Vin  vector of wavelet smooths
	 * @param N    length of Win, Vin
	 * @param k    detail number
	 * @param f    wavelet Filter structure
	 * @param Vout OUT vector of wavelet smooths
	 */

	public static void salvo_imodwt(double[] Win, double[] Vin, int N, int k, 
											WaveletFilter f, double[] Vout) {
		double[] ht = new double[f.length];
		double[] gt = new double[f.length];
		int pow2_k = pow2(k - 1);

		for (int l = 0; l < f.length; l++)
			{
			ht[l] = f.h[l] * INV_SQRT_2;
			gt[l] = f.g[l] * INV_SQRT_2;
			}

		for (int t = 0; t < N; t++)
			{
			int j = t;
			Vout[t] = 0.0;
			for (int l = 0; l < f.length; l++)
				{
				Vout[t] += (ht[l] * Win[j]) + (gt[l] * Vin[j]); /* GMA */
				j = wrap(j + pow2_k, N);                                        /* GMA */
				}
			}
		}
	


	final static double INV_SQRT_2 = 1.0 / Math.sqrt(2.0);
	static final int pow2(int k) { return 1 << k; }

	private static int wrap(int i, int length) {
		if (i >= length) 	return i - length;
		if (i < 0) 			return i + length;
		return i;
	}
}
