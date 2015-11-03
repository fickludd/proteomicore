package se.lth.immun.signal

import scala.Array.canBuildFrom
import scala.util.Random




object Impulses {
	
	val LOW_PASS = Array(
	                        0.000000000000f,
	                        0.002184860849f,
	                        0.000000000000f,
	                       -0.010183381788f,
	                        0.000000000000f,
	                        0.031133369533f,
	                        0.000000000000f,
	                       -0.082780563727f,
	                        0.000000000000f,
	                        0.309793733416f,
	                        0.500000000000f,
	                        0.309793733416f,
	                        0.000000000000f,
	                       -0.082780563727f,
	                        0.000000000000f,
	                        0.031133369533f,
	                        0.000000000000f,
	                       -0.010183381788f,
	                        0.000000000000f,
	                        0.002184860849f,
	                        0.000000000000f
						)
	
	val HIGH_PASS = Array(
                            0.000000000000f,
                           -0.002184860849f,
                            0.000000000000f,
                            0.010183381788f,
                            0.000000000000f,
                           -0.031133369533f,
                            0.000000000000f,
                            0.082780563727f,
                            0.000000000000f,
                           -0.309793733416f,
                            0.500000000000f,
                           -0.309793733416f,
                            0.000000000000f,
                            0.082780563727f,
                            0.000000000000f,
                           -0.031133369533f,
                            0.000000000000f,
                            0.010183381788f,
                            0.000000000000f,
                           -0.002184860849f,
                            0.000000000000f
						)
}




object Synthesizers {
	
	type Signal = Array[Double]
	
	
	
	
	def halfBandCustom(low:Signal, high:Signal):Signal = {
        var signal = new Signal(high.length)
        var lowUpped = Sample.up(low)

        for (n <- 0 until signal.length)
            signal(n) = high(n) + lowUpped(n % lowUpped.length)
        return signal
    }
}




object Decomposers {
	
	type Signal = Array[Double]
    
	
	
	def halfBandCustom(signal:Signal):WaveletLevel = {
        var low = new Signal(signal.length / 2)
        var high = new Signal(signal.length)
        var lowPass = Impulses.LOW_PASS
        
        for (k <- 0 until low.length) {
            low(k) = 0
            for (n <- 0 until signal.length) {
                var filterIndex:Int = 2 * k - (n - 10)
                if (filterIndex >= 0 && filterIndex < lowPass.length) 
                	low(k) += signal(n) * lowPass(filterIndex)
            }
        }

        var lowUpped = Sample.up(low)
        for (k <- 0 until high.length)
        	high(k) = signal(k) - lowUpped(k % lowUpped.length)

        return new WaveletLevel(low, high)
	}
}




object WaveletLevel {
	
	type Signal = Array[Double]
	
	
	
	def decompose(
				signal:Signal,
				nOLevels:Int,
				decomposer:(Signal => WaveletLevel)
			):Array[WaveletLevel] = {
		
        var ret = new Array[WaveletLevel](nOLevels+1)
        ret(0) = new WaveletLevel(signal, Array())

        for (l <- 1 to nOLevels)
            ret(l) = decomposer(ret(l - 1).low)

        return ret
    }
	
	
	
	def synthesize(
				waveLevels:Array[WaveletLevel], 
				nOLevels:Int, 
				synthesizer:(Signal, Signal) => Signal
			):Signal = {
        
		var signal = synthesizer(waveLevels(nOLevels).low, waveLevels(nOLevels).high)
        
        var l = nOLevels - 1
		while (l > 0) {
            signal = synthesizer(signal, waveLevels(l).high)
            l -= 1
		}
		
        return signal.map(d => math.abs(d))
    }
	
	
	
    def generateRandom(
    		waveLevels:Array[WaveletLevel], 
    		nOLevels:Int
    ):Array[WaveletLevel] = {
    	
        var rand = new Array[WaveletLevel](nOLevels+1)
        rand(0) = new WaveletLevel(Array(), Array())
        var regions = new Array[Int](waveLevels(nOLevels).low.length)
        for (i <- 0 until regions.length) regions(i) = Random.nextInt(regions.length)
        var low = regions.map(r => waveLevels(nOLevels).low(r))
        	
        for (w <- 1 to nOLevels) {
            var high = new Signal(waveLevels(w).high.length)

            /*for (l <- 0 until low.length) {
                var index = 	(regions(l * regions.length / low.length) 
                		   	  *	 low.length / regions.length + JTMath.randInt(nOLevels - w))
                low(l) = Math.abs(waveLevels(w).low(index))
            }*/
            if (high.length>=3) for (h <- 0 until high.length) {
                //high[h] = waveLevels[w].high[r.Next(high.Length)];

                var index = (	 regions(h * regions.length / high.length) * high.length / regions.length
                			   - high.length / 6
                			   + Random.nextInt(high.length / 3))
                high(h) = waveLevels(w).high(
                			if (index < 0) 	index + high.length
                			else		 	index % high.length)
            }
            rand(w) = new WaveletLevel(new Signal(0), high)
        }
        rand(nOLevels).low = low

        return rand
    }
}




class WaveletLevel(
		var low:Array[Double],
		var high:Array[Double]
) {}