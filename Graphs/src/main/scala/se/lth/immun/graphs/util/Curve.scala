package se.lth.immun.graphs.util

import scala.collection._
import scala.collection.generic._
import java.awt.Color

/*
class DoubleDataPoint[X](x:X, y:Double) 
	extends DataPoint[X, Double](x, y, java.lang.Double.isNaN(y))
class DataPoint[X, Y](var x:X, var y:Y, var isMissing:Boolean = false) {
	override def toString = if (isMissing) "[missing]" else "["+x+","+y+"]"
}


class CurveBuilder[D](
		var name:String,
		var col:Color = null
) extends scala.collection.mutable.LazyBuilder[D, Curve[D]] {
	def result = {
		val data = parts.foldLeft(List[D]()){(l,n) => l ++ n}
		new Curve(data, name, col)
	}
}

class Curve[D](	
		data : Seq[D],
		var name:String = "",
		var col:Color = null
) extends Traversable[D]
                      with GenericTraversableTemplate[D, Curve]
                      with TraversableLike[D, Curve[D]] {
	
	override def companion = CurveCompanion
	override def genericBuilder[B] = new CurveBuilder[B](name, col)
	override def newBuilder = new CurveBuilder[D](name, col)
	def foreach[U](f: D => U) = data.foreach(f)
	
}

object CurveCompanion extends TraversableFactory[Curve] {  
	implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, Curve[A]] = new GenericCanBuildFrom[A]
	def newBuilder[D] = new CurveBuilder[D]("")
}
*/

/*
class MyColl[A](seq : A*) extends Traversable[A]
                              with GenericTraversableTemplate[A, MyColl] 
                              with TraversableLike[A, MyColl[A]] {
   override def companion = MyColl
   def foreach[U](f: A => U) = util.Random.shuffle(seq.toSeq).foreach(f)
 }
*/

trait CurveLike2[X, Y] {
	def name:String
	def color:Color
	def xs:Seq[X]
	def ys:Seq[Y]
	def missing:Seq[Boolean]
	
	def zoom(xAxis:Axis[X]) = {
		var ix0 = xs.indexWhere(x => xAxis.isVisible(x), 0)
		var ixn = xs.indexWhere(x => !xAxis.isVisible(x), ix0)
		new ProxyCurve2[X, Y](this, ix0, if (ixn == -1) xs.length else ixn)
	}
}



/*
 * data has to be sorted in ascending x order
 */
class Curve2[X, Y](
		val _xs:Seq[X],
		val _ys:Seq[Y],
		val _missing:Seq[Boolean],
		var _name:String = "",
		var _col:Color = null
) extends CurveLike2[X, Y] {
	def name = _name
	def color = _col
	def xs = _xs
	def ys = _ys
	def missing = _missing
}



class ProxyCurve2[X, Y](
		val curve:CurveLike2[X, Y],
		val ix0:Int,
		val ixn:Int
) extends CurveLike2[X, Y] {
	def name = curve.name
	def color = curve.color
	def missing = curve.missing.slice(ix0, ixn)
	def xs = curve.xs.slice(ix0, ixn)
	def ys = curve.ys.slice(ix0, ixn)
}