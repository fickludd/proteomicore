package se.lth.immun.signal;

public class DWaveletLevel {

	
	
	public double[] 	smooth;
	public double[] 	noise;
	public int			length;
	public int			level;
	
	
	
	public DWaveletLevel(
			double[] 	smooth,
			double[] 	noise,
			int			length,
			int			level
	) {
		this.smooth		= smooth;
		this.noise 		= noise;
		this.length		= length;
		this.level 		= level;
	}
	
	
	
	public DWaveletLevel(
			double[] 	smooth
	) {
		this.smooth		= smooth;
		this.noise 		= null;
		this.length		= smooth.length;
		this.level		= 0;
	}
	
	
	
	public DWaveletLevel withSmooth(double[] smooth) {
		return new DWaveletLevel(
				Arrays.realloc(smooth, this.length), 
				this.noise, this.length, this.level);
	}
	
	
	
	public DWaveletLevel zero() {
		return new DWaveletLevel(
				null, new double[this.length], 
				this.length, this.level);
	}
	
	
	
	public DWaveletLevel zeroNoise() {
		return new DWaveletLevel(
				this.smooth, new double[this.length], 
				this.length, this.level);
	}
	
	
	
	public static DWaveletLevel reuse(DWaveletLevel old, int l) {
		if (old == null)
			return new DWaveletLevel(
					new double[l], 
					new double[l], 
					l, 0); 
		else
			return new DWaveletLevel(
				Arrays.realloc(old.smooth, l), 
				Arrays.realloc(old.noise, l), 
				l, 0); 
	}
	
	
	
	public static DWaveletLevel reuseNoise(double[] smooth, DWaveletLevel old, int l) {
		if (old == null)
			return new DWaveletLevel(
					Arrays.realloc(smooth, l), 
					new double[l],
					l, 0);
		else {
			old.smooth = Arrays.realloc(smooth, l);
			old.noise = Arrays.realloc(old.noise, l);
			old.length = l;
			return old;
		}
	}
}
