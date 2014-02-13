package se.lth.immun.math


object Ratios {
	
	type UpIndex = Int
	type DownIndex = Int
	type RelIndex = Int
	
	/*
	 * Iterate performs the 3-index iteration loop. 
	 * 'loop'-function should take (relIndex, upIndex, downIndex)
	 */
	def iterate(noFrags:Int, loop:(RelIndex, UpIndex, DownIndex) => Unit) {
        var relIndex = 0;
        for (i <- 0 until noFrags)
            for (j <- i + 1 until noFrags) {
                loop(relIndex, i, j)
                relIndex += 1
            }
    }

    def getRatioIndexTable(nODataSet:Int):Array[Array[Int]] = {
        var ret = new Array[Array[Int]](nODataSet)
        for (i <- 0 until ret.length) ret(i) = new Array(nODataSet) 
        
        for (i <- 0 until nODataSet) ret(i)(i) = Int.MaxValue
        iterate(nODataSet, (ri, i, j) =>
        {
            ret(i)(j) = ri
            ret(j)(i) = -ri
        })

        return ret
    }

    def getTargetRatioTable[R : ClassManifest](neutral:R)(nODataSet:Int, targets:Array[R], inv:R => R):Array[Array[R]] = {
        var ret = new Array[Array[R]](nODataSet)
        for (i <- 0 until ret.length) ret(i) = new Array(nODataSet) 
        
        for (i <- 0 until nODataSet) ret(i)(i) = neutral
        iterate(nODataSet, (ri, i, j) =>
        {
            ret(i)(j) = targets(ri)
            ret(j)(i) = inv(targets(ri))
        })

        return ret
    }
}




class Ratios(
		var nODataSets:Int = -1,
		var dataSetGroup:Array[Array[Double]] = null
) {
	def this(dataSetGroup:Array[Array[Double]]) = this(-1, dataSetGroup)
	
	var values:Array[Array[Array[Double]]] = Array()
    var indexes:List[RatioIndex] = Nil

    if (nODataSets >= 0) {
    	values = new Array(nODataSets)
        for (i <- 0 until nODataSets)
            values(i) = new Array(nODataSets)
    	
    } else if (dataSetGroup != null) {
        var size = dataSetGroup.length
        values = new Array(size)
        for (i <- 0 until size)
            values(i) = new Array(size);

        Ratios.iterate(size, 
        		(ri, u, d) => addRatio(dataSetGroup(u).zip(dataSetGroup(d)).map(t => t._1 / t._2), u, d))
    }
    
    def length = indexes.length

    def getRatioIndex(index:Int) = indexes(index)

    def getRatioIndexTable() = Ratios.getRatioIndexTable(values.length)

    def getTargetRatioTable[R : ClassManifest](neutral:R)(targets:Array[R], inv:R => R) = Ratios.getTargetRatioTable(neutral)(values.length, targets, inv)

    def addRatio(ratio:Array[Double], upIndex:Int, downIndex:Int) {
        values(upIndex)(downIndex) = ratio
        indexes = indexes ::: (new RatioIndex(upIndex, downIndex, indexes.length) :: Nil)
    }

    def getRatio(index:Int):Array[Double] = values(indexes(index).up)(indexes(index).down)

    def getRatio(upIndex:Int, downIndex:Int):Array[Double] = values(upIndex)(downIndex)
}




class RatioIndex(
		var up:Int,
		var down:Int,
		var ratio:Int
) {
    def inCommonTrans(other:RatioIndex):Boolean = 
    	up == other.up || up == other.down || down == other.up || down == other.down
    
    override def toString():String = "RatioIndex["+up+" / "+down+"] : "+ratio
}

/*
class RatioExtensions {
    public static Ratios toRatios(this IList<IList<double>> dataSetGroup)
    {
        return new Ratios(dataSetGroup);
    }
}*/