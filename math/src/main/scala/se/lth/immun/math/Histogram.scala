package se.lth.immun.math

object Histogram {
	
	def fromArray(a:Array[Double], numBins:Int, logScale:Boolean = false):Histogram = {
		var sorted 		= a.sorted
		var binMax 		= new Array[Double](numBins)
		var bins 		= new Array[Double](numBins)
		var counts 		= new Array[Double](numBins)
		if (logScale) {
			sorted.find(_ > 0.0) match {
				case Some(d) => {
					var min = math.log(d)
					var max = math.log(sorted.last)
					for (i <- 0 until numBins)
						binMax(i) = math.exp(((max - min) * (i+1))/numBins + min)
				}
				case None => 
					throw new IllegalArgumentException("Input array needs"+
							" at least one value > 0.0 if log scale is used")
			}
		} else {
			var min = sorted.head
			var max = sorted.last
			for (i <- 0 until numBins)
				binMax(i) = ((max - min) * (i+1))/numBins + min
		}
		
		bins(0) = (binMax(0) + sorted.head) / 2
		for (i <- 1 until numBins)
			bins(i) = (binMax(i-1) + binMax(i)) / 2
		
		var i = 0
		var b = 0
		while (i < sorted.length && b < numBins) {
			if (sorted(i) < binMax(b)) {
				counts(b) += 1
				i += 1
			} else if (b == numBins - 2) {
				counts(b+1) = sorted.length - i
				i = sorted.length
			} else
				b += 1
		}
		
		return new Histogram(bins, counts)
	}
}

class Histogram(
		var bins:Array[Double],
		var counts:Array[Double]
) {

}