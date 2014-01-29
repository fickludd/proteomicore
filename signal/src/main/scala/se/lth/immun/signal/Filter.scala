package jt.signal

import scala.annotation.unchecked
import org.apache.commons.math3.stat.StatUtils

object Filter {
	
	val MIN_VALUE = java.lang.Double.MIN_VALUE
	
	def savitzkyGolay9(a:Array[Double]):Array[Double] = {
	
		if (a.length < 9)
            return a.clone
        
        var retArr = new Array[Double](a.length)
        var j = a.length
        for (i <- 0 until 4) {
        	j -= 1
        	retArr(i) = a(i)
        	retArr(j) = a(j)
        }
        
        for (i <- 4 until a.length - 4) {
            retArr(i) = (0.417 * a(i) +
            	+ 0.315 * (a(i-1) + a(i+1))
            	+ 0.070 * (a(i-2) + a(i+2))
            	- 0.128 * (a(i-3) + a(i+3))
            	+ 0.035 * (a(i-4) + a(i+4)))
        	/*var sum = 59 * a(i)
                + 54 * (a(i - 1) + a(i + 1))
                + 39 * (a(i - 2) + a(i + 2))
                + 14 * (a(i - 3) + a(i + 3))
                - 21 * (a(i - 4) + a(i + 4))
            retArr(i) = sum / 231
            */
        }
        return retArr map (d => math.max(MIN_VALUE, d))
	}


    def minLine(precursorSignal:Array[Array[Double]]):Array[Double] = {
    	
        if (precursorSignal.length == 0)
            throw new IllegalArgumentException("Empty dataSetGroup!")

        var l = precursorSignal.map(f => f.length).max
        var retArr = new Array[Double](l)
        for (i <- 0 until l) {
            retArr(i) = precursorSignal.map(f => if (f.length > i) f(i) else Double.MaxValue).min
        }
        return retArr
    }


    def baseLineReduce(precursorSignal:Array[Array[Double]]):Array[Array[Double]] = {
        var min = minLine(precursorSignal)
        var median = StatUtils.percentile(min, 50)
        var limitedMin = min map (a => math.min(2 * median, a))
        
        var zips = precursorSignal map (fragment => fragment zip limitedMin)
        var reduces = zips map (pairs => pairs map (t => t._1 - t._2))
        
        return precursorSignal map (fragment => ((fragment zip limitedMin) map (t => t._1 - t._2)))
    }
}