package se.lth.immun.signal;

public class SalvatorDenoiser {

	double minDiff 			= 0.02;
	boolean average 		= true;
	int numWaveletLevels 	= 6;
	
	

	public SalvatorDenoiser(
			double 		minDiff, 
			boolean 	average, 
			int 		numWaveletLevels
	) {
		this.minDiff = minDiff;
		this.average = average;
		this.numWaveletLevels = numWaveletLevels;
	}
	
	
	
	public double[] denoise(
			double[] data
	) {
		return data;
	}
}
