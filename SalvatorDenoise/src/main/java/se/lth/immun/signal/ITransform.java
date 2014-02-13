package se.lth.immun.signal;

public interface ITransform {

	public int nextLevelLength(int length);
	public int prevLevelLength(int length);
	
	public String inDataOk(
			double[] 		in, 
			int 			length
		);
	
	public void transform(
						DWaveletLevel 	in, 
						WaveletFilter 	f, 
						DWaveletLevel	out
					);

	public void inverse(
						DWaveletLevel 	in, 
						WaveletFilter 	f, 
						DWaveletLevel	out
					);
}
