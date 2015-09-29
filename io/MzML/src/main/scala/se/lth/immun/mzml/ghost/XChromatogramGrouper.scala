package se.lth.immun.mzml.ghost

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap

object XChromatogramGrouper {
	val DEFAULT_Q1_TOLERANCE = 0.7
	val DEFAULT_Q3_TOLERANCE = 0.7
	val DEFAULT_INTERNAL_TOLERANCE = 0.0001
}

class XChromatogramGrouper {
	import XChromatogramGrouper._
	
	val q1s 	= new ArrayBuffer[Double]
	val groups 	= new HashMap[Double, XChromatogramGroup]
	
	
	
	def add(c:XChromatogram) = {
		var group:XChromatogramGroup = null
		extractGroup(c.q1, DEFAULT_INTERNAL_TOLERANCE) match {
			case Some(g) => group = g
			case None => {
				group = new XChromatogramGroup(c.q1)
				q1s += c.q1
				groups += c.q1 -> group
			}
		}
		group.chromatograms += c
	}

	
	
    def getClosestQ1(mz:Double, tolerance:Double = DEFAULT_Q1_TOLERANCE):Double = {
        var best:Double = -1
        var minDiff = Double.MaxValue
        for (q1 <- q1s) {
            var diff = Math.abs(q1 - mz)
            if (diff < minDiff) {
                minDiff = diff
                best = q1
            }
        }

        if (minDiff > tolerance)
            return -1

        return best
    }
	
	
	
	def extractGroup(
			q1:Double, 
			tolerance:Double = DEFAULT_Q1_TOLERANCE
	):Option[XChromatogramGroup] = {
		getClosestQ1(q1, tolerance) match {
			case -1 	=> return None
			case x 		=> return Some(groups(x))
		}
	}
    
	
    
    def extractGroup(
    		q1:Double, 
    		q3s:Array[Double], 
    		unique:Boolean
    ):Option[XChromatogramGroup] = {
    	getClosestQ1(q1) match {
			case -1 	=> return None
			case x 		=> return Some(groups(x).filter(q3s, unique, DEFAULT_Q3_TOLERANCE))
		}
    }

}