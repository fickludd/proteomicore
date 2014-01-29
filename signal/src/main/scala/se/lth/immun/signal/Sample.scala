package se.lth.immun.signal

import scala.collection.mutable.ArrayBuffer

object Sample {
	
	type Signal = Seq[Double]
	type ASignal = Array[Double]
	
	/*
	 * Constructs ramp from x0 to and including xn in n steps.
	 */
	def ramp(x0:Double, xn:Double, n:Int):Signal = {
		var dx = (xn - x0) / (n-1)
		var a = new ArrayBuffer[Double]
		var cx = x0
		for (i <- 0 until n) {
			a += cx
			cx += dx
		}
		return a.toArray
	}
	
	/*
	 * Samples the signal ys measured at the timepoints xs on the timepoint x2, 
	 * returning pad if x2 is outside xs. 
	 */
	def sample(xs:Signal, ys:Signal, x2:Double, pad:Double = 0.0):Double = {
		var xi = 0
		while (xi < (xs.length - 1) && xs(xi+1) < x2) xi+=1
		return if (xs(xi) < x2 && xi < (xs.length - 1))
					blend(ys(xi), ys(xi+1), (x2-xs(xi)) / (xs(xi+1) - xs(xi)))
				else
					pad
	}
	
	/*
	 * Blend the double a and b with the factor k (0<= k <= 1), were 
	 * 	k=0 => 100% a
	 * 	k=1 => 100% b
	 */
	def blend(a:Double, b:Double, k:Double):Double = (1.0-k)*a + k*b
	
	/*
	 * Resamples the signal ys measured at the timepoints xs on the timepoints x2s, 
	 * returning pad at any x2s is outside xs. 
	 */
	def resample(xs:Signal, ys:Signal, x2s:Signal, pad:Double = 0.0):Signal = {
		if (xs.length == 0)
			return x2s.map(y => pad)
		var xi = 0
		var y2s = List[Double]()
		for (x2 <- x2s) {
			while (xi < (xs.length - 1) && xs(xi+1) < x2) xi+=1
			if (xi < (xs.length - 1) && xs(xi) < x2)
				y2s = blend(ys(xi), ys(xi+1), (x2-xs(xi)) / (xs(xi+1) - xs(xi))) :: y2s 
			else
				y2s = pad :: y2s
		}
		return y2s.reverse
	}
	
	def up(signal:Signal):Signal = {
        var output = new ASignal(signal.length * 2)
        for (i <- 0 until (signal.length - 1)) {
            output(i * 2) = signal(i)
            output(i * 2 + 1) = (signal(i) + signal(i+1)) / 2
        }
        output(output.length - 2) = signal(signal.length - 1)
        output(output.length - 1) = (signal(signal.length - 1) + signal(0)) / 2
        return output
    }
	
	def sub(signal:Signal):Signal = {
        var output = new ASignal(signal.length / 2)
        for (i <- 0 until output.length) output(i) = signal(2 * i)
        return output;
    }
}