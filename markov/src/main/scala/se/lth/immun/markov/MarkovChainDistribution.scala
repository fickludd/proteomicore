package se.lth.immun.markov

import se.lth.immun.math.Matrix



object MarkovChainDistribution {
	
	/**
	 * Sample usage. Should print
	 * 
	 *  K1
	 *  0.0e+00 0.0e+00 0.0e+00 0.0e+00 0.0e+00 0.0e+00 0.0e+00
	 *  4.0e-01 2.7e-01 1.5e-01 8.2e-02 4.5e-02 2.5e-02 0.0e+00
	 *  3.6e-01 2.6e-01 1.6e-01 9.2e-02 5.4e-02 0.0e+00 0.0e+00
	 *  3.2e-01 2.5e-01 1.6e-01 1.0e-01 0.0e+00 0.0e+00 0.0e+00
	 *  2.9e-01 2.4e-01 1.6e-01 0.0e+00 0.0e+00 0.0e+00 0.0e+00
	 *  2.6e-01 2.3e-01 0.0e+00 0.0e+00 0.0e+00 0.0e+00 0.0e+00
	 *  2.4e-01 0.0e+00 0.0e+00 0.0e+00 0.0e+00 0.0e+00 0.0e+00
	 *  K2
	 *  0.0e+00 6.0e-01 3.3e-01 1.8e-01 1.0e-01 5.5e-02 3.0e-02
	 *  0.0e+00 4.0e-02 4.9e-02 4.2e-02 3.1e-02 2.2e-02 0.0e+00
	 *  0.0e+00 3.6e-02 4.6e-02 4.1e-02 3.2e-02 0.0e+00 0.0e+00
	 *  0.0e+00 3.2e-02 4.3e-02 4.0e-02 0.0e+00 0.0e+00 0.0e+00
	 *  0.0e+00 2.9e-02 4.0e-02 0.0e+00 0.0e+00 0.0e+00 0.0e+00
	 *  0.0e+00 2.6e-02 0.0e+00 0.0e+00 0.0e+00 0.0e+00 0.0e+00
	 *  0.0e+00 0.0e+00 0.0e+00 0.0e+00 0.0e+00 0.0e+00 0.0e+00
	 *  pdf    sum:1.0000000000000004
	 *  2.4e-01 2.6e-01 2.0e-01 1.4e-01 8.6e-02 4.6e-02 3.0e-02 
	 */
	def main(args:Array[String]) = {
		var mcd = new MarkovChainDistribution(6, 0.4, 0.9, 0.55)
		println("K1")
		mcd.K1.foreach(r => println(r.map(p => "%.1e".format(p)).mkString(" ")))
		println("K2")
		mcd.K2.foreach(r => println(r.map(p => "%.1e".format(p)).mkString(" ")))
		
		var pdf = mcd.pdf(6)
		println("pdf    sum:"+pdf.sum)
		println(pdf.map(p => "%.1e".format(p)).mkString(" "))
	}
}



/**
 * Representation of the distributions given by a Markov 2-state model,
 * backed up by dynamic programming. Initiation is O(2 * n^2), and acquisition
 * of the probability density function of length m O(m).
 */
class MarkovChainDistribution(
		val n:Int,
		val p1:Double,
		val p11:Double,
		val p22:Double
) {
	
	if (n < 1)
		throw new IllegalArgumentException("n must be > 0, got n="+n)
	if (p1 < 0 || p1 > 1)
		throw new IllegalArgumentException("need <= p1 <= 1.0, got p1="+n)
	if (p11 < 0 || p11 > 1)
		throw new IllegalArgumentException("need 0.0 <= p11 <= 1.0, got p11="+n)
	if (p22 < 0 || p22 > 1)
		throw new IllegalArgumentException("need 0.0 <= p22 <= 1.0, got p22="+n)

	val p2:Double	= 1 - p1
	val p12:Double	= 1 - p11
	val p21:Double	= 1 - p22
	val K1 = Matrix.get2d[Double](n)
	val K2 = Matrix.get2d[Double](n)
	
	K1(0)(0) = p1
	K2(0)(0) = p2
	
	for (i <- 1 until n) {
		K1(i)(0) = K1(i-1)(0) * p11
		K2(i)(0) = K1(i-1)(0) * p12
		K2(0)(i) = K2(0)(i-1) * p22
		K1(0)(i) = K2(0)(i-1) * p21
		for (j <- 1 until i) {
			val t1 = i-j
			val t2 = j
			K1(t1)(t2) += K1(t1-1)(t2) * p11
			K1(t1)(t2) += K2(t1)(t2-1) * p21
			K2(t1)(t2) += K1(t1-1)(t2) * p12
			K2(t1)(t2) += K2(t1)(t2-1) * p22
		}
	}

	/**
	 * Gives the probability density function for a markov chain of length m
	 * 
	 * @returns 	Array of length m+1 where the first element corresponds 
	 * 				to all time points being T1 and the last to all time points
	 * 				being T2. 
	 */
	def pdf(m:Int):Array[Double] = {
		if (m > n || m < 1)
			throw new IllegalArgumentException("m must be > 0 and <= n, where n="+n+", got m="+m)
		val x = new Array[Double](m+1)
		x(0) = K1(m-1)(0)
		x(m) = K2(0)(m-1)
		for (i <- 1 until m)
			x(i) = K1(m-i-1)(i)+K2(m-i)(i-1)
		return x
	}
	
	
	
	override def toString = toString(n)
		
		
		
	def toString(m:Int) = 
		"p1:%.1e    p11:%.1e     p22:%.1e    pdf:".format(p1, p11, p22)+
		pdf(n).map(p => "%.1e".format(p)).mkString(" ")
}
