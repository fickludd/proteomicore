package se.lth.immun.math

import scala.collection.LinearSeq
import scala.util.Random

object Stats {
	
	/**
	 * Draws random sample for collection, with or without replacement
	 */
	def draw[T](arr:Seq[T], replace:Boolean = true):Tuple2[Seq[T], T] = {
		var i = Random.nextInt(arr.length)
		new Tuple2((if (replace) arr else arr.take(i) ++ arr.drop(i+1)), arr(i))
	}
	
	
	
	def mean(arr:Seq[Double]):Double = arr.length match {
		case 0 => Double.NaN
		case 1 => arr(0)
		case l:Int => arr.sum / l
	}
	
	
	
	private def _median(arr:Array[Double]):Double = quickSelect(arr, arr.length / 2)
	def median(arr:Array[Double]):Double = arr.length match {
		case 0 => Double.NaN
		case 1 => arr(0)
		case _ => _median(arr.clone())
	}
	
	
	
	def partition(arr:Seq[Double], pivot:Double):Tuple3[List[Double], List[Double], List[Double]] = {
		var L1:List[Double] = Nil
		var L2:List[Double] = Nil
		var L3:List[Double] = Nil
		
		arr.foreach(d => 	if (d < pivot) 
								L1 = d :: L1
							else if (d == pivot) 
								L2 = d :: L2
							else
								L3 = d :: L3
					)
		
		return new Tuple3(L1, L2, L3)
	}
	
	
	
	def quickSelect(arr:Seq[Double], k:Int):Double = {
		if (k >= arr.length)
			throw new IllegalArgumentException("Rank '"+k+"' is larger that array size ("+arr.length+").")
		
		if (arr.length < 10) {
			var sort = arr.sorted
			return sort(k)
		}
		
		var M = arr(Random.nextInt(arr.length))
		
		var lists = partition(arr, M)
		if (k < lists._1.length)
			return quickSelect(lists._1.toArray, k)
		else if (k >= lists._1.length + lists._2.length)
			return quickSelect(lists._3.toArray, k - lists._1.length - lists._2.length)
		else return M
	}
	
	
	
	def summedSquares(arr:Seq[Double], mean:Double = Double.NaN):Double = {
		if (arr.length < 2) return Double.NaN

        var ss = 0.0
        var _m = if (java.lang.Double.isNaN(mean)) this.mean(arr) else mean
        for (v <- arr)
            ss += (v - _m) * (v - _m)
            
        return ss
	}
	
	
	def variance(arr:Seq[Double], mean:Double = Double.NaN):Double = {
        if (arr.length < 2) return Double.NaN

        return summedSquares(arr, mean) / (arr.length - 1)
    }
	
	
	
	def varianceP(arr:Seq[Double], mean:Double = Double.NaN):Double = {
        if (arr.length < 2) return Double.NaN

        return summedSquares(arr, mean) / (arr.length)
    }


    def sd(arr:Seq[Double], mean:Double = Double.NaN):Double = {
        if (arr.length < 2) return Double.NaN
        return math.sqrt(variance(arr, mean))
    }

    
    
    def sdP(arr:Seq[Double], mean:Double = Double.NaN):Double = {
        if (arr.length < 2) return Double.NaN
        return math.sqrt(varianceP(arr, mean))
    }

    
    
    def cv(arr:Seq[Double]):Double = {
        if (arr.length < 2) return Double.NaN
        return sd(arr) / mean(arr)
    }
    
    
    
    def mad(arr:Array[Double]):Double = arr.length match {
    	case 0 => return 0.0
    	case 1 => return arr(0)
    	case _ => {
	        var median = _median(arr.clone)
	
	        var diff = arr.clone
	        for (i <- 0 until diff.length) 
	        	diff(i) = math.abs(arr(i) - median)
	
	        return this.median(diff)
    	}
    }
	
	
	/**
	 * Pearson correlation
	 */
	def pearsonCorrelation(obs1:Array[Double], obs2:Array[Double]):Double = {
		var m1 		= mean(obs1)
		var m2 		= mean(obs2)
		var s1 		= sd(obs1, m1)
		var s2 		= sd(obs2, m2)
		var sxy 	= 0.0
		var n 		= obs1.length

		if (s1 == 0 && s2 == 0) return 1.0
		if (s1 == 0 || s2 == 0) return 0.0
		
		for (t <- 0 until n)
			sxy += obs1(t) * obs2(t)
		
		return (sxy - n*m1*m2) / ((n-1)*s1*s2)
	}
}