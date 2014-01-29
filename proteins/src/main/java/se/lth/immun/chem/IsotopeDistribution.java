package se.lth.immun.chem;

/**
 * Implemented based on
 * 
 * Calculation of isotope distributions in mass spectrometry. A trivial solution for a non-trivial problem
 *  Hugo Kubinyi, 1991
 */
public class IsotopeDistribution {

	public static float PRUNE_LEVEL = 0.000001f;
	
	public int m0 = 0;
	public int dm = 1;
	public float[] intensities = {1.0f};
	
	public IsotopeDistribution() {}
	
	public void add(IsotopeDistribution x) {
		m0 += x.m0;
		float[] ni 	= new float[dm + x.dm - 1];
		for (int i = 0; i < dm; i++)
			for (int j = 0; j < x.dm; j++) {
				ni[i + j] += intensities[i] * x.intensities[j];
			}
		
		dm = dm + x.dm - 1;
		while (ni[dm-1] < PRUNE_LEVEL)
			dm--;
		intensities = ni;
	}
	
	public IsotopeDistribution copy() {
		IsotopeDistribution i = new IsotopeDistribution();
		i.m0 = m0;
		i.dm = dm;
		i.intensities = new float[intensities.length];
		for (int j = 0; j < intensities.length; j++)
			i.intensities[j] = intensities[j];
		return i;
	}
	
	public IsotopeDistribution mult(int n) {
		if (n == 1)
			return copy();
		
		IsotopeDistribution i = new IsotopeDistribution();
		
		IsotopeDistribution prev = this;
		IsotopeDistribution next = null;

		int k = 1;
		while (k <= n) {
			if ((k & n) != 0)
				i.add(prev);
			next = prev.copy();
			next.add(prev);
			prev = next;
			k = k << 1;
		}
		
		return i;
	}
}
