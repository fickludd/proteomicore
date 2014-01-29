package se.lth.immun.signal



class IndexPeak(
		var start:Int,
		var peak:Int,
		var end:Int,
		var height:Double
) {
	def width = end - start
}



abstract class PeakFinder {
	
	def findPeak(precursorSignal:Array[Array[Double]]):IndexPeak
}



class PartMaxPeakFinder(
		var edgeRatio:Double
) extends PeakFinder {
	
	override def findPeak(precursorSignal:Array[Array[Double]]):IndexPeak = {
		var max = Double.MinValue
		var maxPos:Int = -1
		var bestDS:Array[Double] = null
		
		for (fragment <- precursorSignal)
			for (i <- 0 until fragment.length)
				if (fragment(i) > max) {
					max = fragment(i)
					maxPos = i
					bestDS = fragment
				}
		
		var leftPos = maxPos
		while (leftPos > 0 && bestDS(leftPos) > max * edgeRatio)
			leftPos -= 1
				
		var rightPos = maxPos
		while (rightPos < bestDS.length - 1 && bestDS(rightPos) > max * edgeRatio)
			rightPos += 1
		
		return new IndexPeak(leftPos, maxPos, rightPos + 1, max)
	}
	
	override def toString = edgeRatio + " Max Peak Finder"
}



class SummedMaxPeakFinder(
		var edgeRatio:Double
) extends PeakFinder {
	
	override def findPeak(precursorSignal:Array[Array[Double]]):IndexPeak = {
		var max = Double.MinValue
		var maxPos:Int = -1
		var total = new Array[Double]((precursorSignal map (f => f.length)).min)

        for (i <- 0 until total.length) {
            total(i) = (precursorSignal map (frag => frag(i))).reduceLeft(_+_)
            if (total(i) > max)
            {
                max = total(i)
                maxPos = i
            }
        }
		
		var limit = max * edgeRatio
		
        var leftPos = maxPos
        while (leftPos > 0 && total(leftPos-1) > limit)
        	leftPos -= 1
        
        var rightPos = maxPos
        while (rightPos + 1 < total.length && total(rightPos + 1) > limit)
        	rightPos += 1
		
		return new IndexPeak(leftPos, maxPos, rightPos + 1, max)
	}
	
	override def toString = edgeRatio + " Summed Max Peak Finder"
}
