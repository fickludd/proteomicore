package se.lth.immun.signal;

import static org.junit.Assert.*;
import org.junit.Test;

public class DWTTest {

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
	double[] x2smooth = {
			42.9552, 31.8936, 52.9621, 78.0718,  21.3281, 
			37.3796, 79.5100, 58.2707, 120.4154, 58.2994,
			39.4278, 35.5531, 26.5144, 49.9341,  93.4266,
			62.4046, 64.7008, 25.0985, 86.8983
		};
	double[] x2noise = {
			15.5524, -5.7833, 0.9195, -30.4736, -30.2077,
			0.5613,  7.6052, 48.8182, -42.6953,  23.8970,
			-19.7241, 15.9106, 3.4185, -2.9047, -24.6814
			-22.5645, 56.6328,-20.1175, 7.4713
		};
	WaveletFilter f = new WaveletFilter(WaveletFilter.Type.LA8);
	DWT dwt = new DWT();
	
	
	
	@Test
	public void dwt() {
		DWaveletLevel in 	= new DWaveletLevel(x);
		DWaveletLevel out 	= new DWaveletLevel(new double[10], new double[10], 10, 0);
		
		dwt.transform(in, f, out);
		assertEquals(0, in.level);
		assertEquals(20, in.length);
		for (int i = 0; i < 20; i++) 
			assertEquals(x[i], in.smooth[i], Double.MIN_VALUE);
		assertEquals(1, out.level);
		assertEquals(10, out.length);

		for (int i = 0; i < 10; i++) {
			assertTrue(out.smooth[i] != 0.0);
			assertTrue(out.noise[i] != 0.0);
		}
	}
	

	
	@Test
	public void idwt() {
		DWaveletLevel in 	= new DWaveletLevel(x);
		DWaveletLevel out 	= new DWaveletLevel(new double[10], new double[10], 10, 0);
		DWaveletLevel out2 	= new DWaveletLevel(new double[20]);

		dwt.transform(in, f, out);
		dwt.inverse(out, f, out2);
		assertEquals(1, out.level);
		assertEquals(10, out.length);
		assertEquals(0, out2.level);
		assertEquals(20, out2.length);
		
		for (int i = 0; i < 20; i++) 
			assertEquals(x[i], out2.smooth[i], 0.001);
	}
	
	
	
	@Test
	public void decomposeError() {
		try {
			Wavelet.decompose(x, 20, 3, f, dwt, Wavelet.Boundary.PERIODIC, null);
			fail("didn't throw error for non-dyadid input");
		} catch (IllegalArgumentException e) {
			
		} catch (Exception e) {
			fail("Threw wrong kind of error for non-dyadid input: "+e.getLocalizedMessage());
		}
	}
	
	

	@Test
	public void decomposePeriodic() {
		DWaveletLevel[] out = Wavelet.decompose(x2, 32, 3, f, dwt, 
									Wavelet.Boundary.PERIODIC, null);
		assertEquals(3, out.length);
		
		assertEquals(16, out[0].length);
		assertEquals(1, out[0].level);
		assertNotNull(out[0].noise);
		assertNull(out[0].smooth);
		
		assertEquals(8, out[1].length);
		assertEquals(2, out[1].level);
		assertNotNull(out[1].noise);
		assertNull(out[1].smooth);
		
		assertEquals(4, out[2].length);
		assertEquals(3, out[2].level);
		assertNotNull(out[2].noise);
		assertNotNull(out[2].smooth);
	}
	
	

	@Test
	public void decomposeReflective() {
		DWaveletLevel[] out = Wavelet.decompose(x2, 32, 3, f, dwt, 
								Wavelet.Boundary.REFLECTIVE, null);
		assertEquals(3, out.length);
		
		assertEquals(32, out[0].length);
		assertEquals(1, out[0].level);
		assertNotNull(out[0].noise);
		assertNull(out[0].smooth);
		
		assertEquals(16, out[1].length);
		assertEquals(2, out[1].level);
		assertNotNull(out[1].noise);
		assertNull(out[1].smooth);
		
		assertEquals(8, out[2].length);
		assertEquals(3, out[2].level);
		assertNotNull(out[2].noise);
		assertNotNull(out[2].smooth);
	}
	
	

	@Test
	public void composePeriodic() {
		int numLevels = 3;
		DWaveletLevel[] out = Wavelet.decompose(x2, 32, numLevels, f, dwt, 
											Wavelet.Boundary.PERIODIC, null);
		double[] out2 = Wavelet.compose(out, numLevels, f, dwt, null);

		for (int i = 0; i < 32; i++) 
			assertEquals(x2[i], out2[i], 0.001);
	}
	
	

	@Test
	public void composeReflective() {
		int numLevels = 3;
		DWaveletLevel[] out = Wavelet.decompose(x2, 32, numLevels, f, dwt, 
										Wavelet.Boundary.REFLECTIVE, null);
		double[] out2 = Wavelet.compose(out, numLevels, f, dwt, null);

		for (int i = 0; i < 32; i++) 
			assertEquals(x2[i], out2[i], 0.001);
	}
}
