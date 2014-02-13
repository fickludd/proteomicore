package se.lth.immun.mzml.ghost

import scala.collection.mutable.ArrayBuffer

class XChromatogramGroup(
		val q1:Double = -1,
		val chromatograms:ArrayBuffer[XChromatogram] = new ArrayBuffer[XChromatogram]
) {
	
	def length = chromatograms.length
	override def toString = q1 + "["+chromatograms.length+"|"+chromatograms.toString+"]\n"
	
	
	
	def filter(q3s:Array[Double], unique:Boolean, q3Tolerance:Double):XChromatogramGroup = {
		val ret = new XChromatogramGroup(q1)
        val fragments = chromatograms.map(_.q3).toArray


        for (i <- 0 until q3s.length) {
            var minDiff = Double.MaxValue
            var bestIndex:Int = -1
            for (j <- 0 until fragments.length) {
                val diff = Math.abs(q3s(i) - fragments(j))
                if (diff < minDiff) {
                    minDiff = diff
                    bestIndex = j
                }
            }

            if (minDiff < q3Tolerance) {
                ret.chromatograms += chromatograms(bestIndex)
                if (unique) fragments(bestIndex) = -100
            }
        }
        return ret
	}
	
	
	
	def extract(q3s:Array[Double], unique:Boolean, q3Tolerance:Double):XChromatogramGroup = {
		val ret = new XChromatogramGroup(q1)
        val fragments = chromatograms.map(_.q3).toArray
        
        val diffs = (
        		for {
	        		ifr <- 0 until fragments.length
	        		iq3 <- 0 until q3s.length
	        	} yield (ifr, iq3, math.abs(q3s(iq3) - fragments(ifr)))
	        ).filter(_._3 < q3Tolerance).sortBy(_._3)
	    
	    val availableChroms = chromatograms.toArray
	    val returnedChroms = new Array[XChromatogram](q3s.length)
	    
	    for ((iavail, iret, diff) <- diffs) 
	    	if (availableChroms(iavail) != null && returnedChroms(iret) == null) {
	    		returnedChroms(iret) = availableChroms(iavail)
	    		availableChroms(iavail) = null
	    		
	    	}
	    
		ret.chromatograms ++= returnedChroms
        return ret
	}
	
	
	
	def resample():XChromatogramGroup = {
		if (chromatograms.forall(_.times.length == 0))
			return new XChromatogramGroup(q1, 
					chromatograms.map(c => new XChromatogram(
												q1,
												c.q3,
												c.ce,
												Array(0.0, 0.0, 0.0),
												Array(0.0, 0.5, 1.0)
											)))
		
		val t0 = chromatograms.filter(_.times.length > 0).map(_.times(0))
		val tn = chromatograms.filter(_.times.length > 0).map(_.times.last)
	    
		val diffs = chromatograms(0).times.zip(chromatograms(0).times.tail).map(t => t._2 - t._1)
	    val dt = diffs.sum / diffs.length
	    
	    val t0Min = t0.min
	    val tnMax = tn.max
	    
	    val length = ((tnMax - t0Min) / dt).toInt
	    val times = new ArrayBuffer[Double]
	    var t = t0Min
	    while (t < tnMax) {
	    	times += t
	    	t += dt
	    }
		
		val ret = new XChromatogramGroup(q1)
        for (c <- chromatograms) {
	        ret.chromatograms += new XChromatogram(
    								c.q1, 
    								c.q3,
    								c.ce,
    								resampleArray(c.times, c.intensities, times).toArray, 
    								times.toArray 
    							)
        }
		
		return ret
	}
	
	
	// ====================================================================
	// The following two methods were injected here from the signal package
	// to avoid the unnecessary dependency 
	// ====================================================================
	
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
	def resampleArray(xs:Seq[Double], ys:Seq[Double], x2s:Seq[Double], pad:Double = 0.0):Seq[Double] = {
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
}