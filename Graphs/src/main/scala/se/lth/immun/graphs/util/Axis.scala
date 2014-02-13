package se.lth.immun.graphs.util

import java.awt.Graphics2D
import java.awt.FontMetrics
import org.apache.commons.math3.stat.StatUtils
import java.util.Date
import java.util.Locale
import java.text.DateFormat

abstract class Axis[T] {
	
	var min:T
	var max:T
	var pad:Double
	var renderer:AxisRenderer[T]
	
	def reversePad = (1 - 1 / (1+pad*2)) / 2
	def doPad(gx:Double) = { var f = 1 / (1+pad*2); gx*f + (1-f)/2 }
	def dePad(gx:Double) = { var f = 1 / (1+pad*2); (gx - (1-f)/2) / f }
	def x2gx(x:T):Double
	def gx2x(gx:Double):T
	def isVisible(x:T):Boolean
	def zoom(min:T, max:T):Unit
	def zoom(values:Seq[T]):Unit
	def minOf(values:Traversable[T], missing:Traversable[Boolean] = null):T
	def maxOf(values:Traversable[T], missing:Traversable[Boolean] = null):T
	
	def magnification(tMin:T, tMax:T) = 1.0
}



class LinearAxis(
		var min:Double,
		var max:Double,
		var pad:Double = 0.1,
		var lockZero:Boolean = false
) extends Axis[Double] {
	
	var renderer:AxisRenderer[Double] = new LinearAxisRenderer
	
	def length = max - min
	
	def x2gx(x:Double):Double = {
		if (x < min) 			doPad(0.0)
		else if (x > max) 		doPad(1.0)
		else					doPad((x - min) / (max - min))
	}
	
	def gx2x(gx:Double):Double = 
		dePad(gx) * length + min	  
	
	def isVisible(x:Double) = min <= x && x <= max
	
	def zoom(min:Double, max:Double) = {
		if (lockZero) {
			this.min = 0.0
			this.max = if (max <= 0.0) 1.0 else max
		} else {
			this.min = min
			this.max = max	
		}
	}
	
	def zoom(values:Seq[Double]) = {
		var filtered = values.filter(d => !java.lang.Double.isNaN(d))
		zoom(filtered.min, filtered.max)
	}
	
	def minOf(values:Traversable[Double], missing:Traversable[Boolean] = null):Double = {
		if (missing == null) return values.min
		var min = Double.MaxValue
		var vit = values.toIterator
		var mit = missing.toIterator
		while (vit.hasNext && mit.hasNext) {
			var v = vit.next
			if (!mit.next && v < min)
				min = v
		}
		return min
	}
	def maxOf(values:Traversable[Double], missing:Traversable[Boolean] = null):Double = {
		if (missing == null) return values.max
		var max = Double.MinValue
		var vit = values.toIterator
		var mit = missing.toIterator
		while (vit.hasNext && mit.hasNext) {
			var v = vit.next
			if (!mit.next && v > max)
				max = v
		}
		return max
	}
	
	override def magnification(tMin:Double, tMax:Double) = 
		(tMax - tMin) / (max-min)
}



class LogAxis(
		var min:Double,
		var max:Double,
		var pad:Double = 0.1
) extends Axis[Double] {
	
	var renderer:AxisRenderer[Double] = new LogAxisRenderer
	private var lmin = Double.NaN
	private var lmax = Double.NaN
	private def length = lmax - lmin
	
	zoom(min, max)
	
	def x2gx(x:Double):Double =
		if (!isVisible(x)) Double.NaN
		else {
			var lx = math.log10(x)
			doPad((lx - lmin) / length)
		}
	
	def gx2x(gx:Double):Double = 
		math.pow(10, dePad(gx) * length + lmin)
	
	def isVisible(x:Double) = !(x < min || x > max)
	
	def zoom(min:Double, max:Double) = {
		if (java.lang.Double.isNaN(min) || min <= 0.0 ||
			java.lang.Double.isNaN(max) || max <= 0.0 ) 
			throw new NumberFormatException("Improper border values for LogAxis, need x > 0.0")
		
		this.min = min
		this.max = max
		this.lmin = math.log10(min)
		this.lmax = math.log10(max)
	}
	
	def zoom(values:Seq[Double]) = {
		var filtered = values.filter(d => !java.lang.Double.isNaN(d))
		zoom(filtered.min, filtered.max)
	}
	
	def minOf(values:Traversable[Double], missing:Traversable[Boolean] = null):Double = {
		if (missing == null) return values.min
		var min = Double.MaxValue
		var vit = values.toIterator
		var mit = missing.toIterator
		while (vit.hasNext && mit.hasNext) {
			var v = vit.next
			if (!mit.next && v < min)
				min = v
		}
		return min
	}
	def maxOf(values:Traversable[Double], missing:Traversable[Boolean] = null):Double = {
		if (missing == null) return values.max
		var max = Double.MinValue
		var vit = values.toIterator
		var mit = missing.toIterator
		while (vit.hasNext && mit.hasNext) {
			var v = vit.next
			if (!mit.next && v > max)
				max = v
		}
		return max
	}
	
	override def toString = "LogAxis(min="+min+", max="+max+", lmin="+lmin+", lmax="+lmax+")"
}



class DateAxis(
		var min:Date,
		var max:Date,
		var pad:Double = 0.1
) extends Axis[Date] {
	
	var renderer:AxisRenderer[Date] = new DateAxisRenderer
	
	private def length = max.getTime - min.getTime
	
	def x2gx(x:Date):Double = {
		if (x.before(min) || x.after(max)) 
			Double.NaN
		else 
			doPad((x.getTime - min.getTime).toDouble / (max.getTime - min.getTime))	
	}
	
	def gx2x(gx:Double):Date = 
		new Date((dePad(gx) * length + min.getTime).toLong)	  
	
	def isVisible(x:Date) = !x.before(min) && !x.after(max)
	
	def zoom(min:Date, max:Date) = {
		this.min = min
		this.max = max
	}
	
	def zoom(values:Seq[Date]) = {
		this.min = minOf(values)
		this.max = maxOf(values)
	}
	
	def minOf(values:Traversable[Date], missing:Traversable[Boolean] = null):Date = {
		if (missing == null) return new Date(values.map(_.getTime).min)
		var min = Long.MaxValue
		var vit = values.toIterator
		var mit = missing.toIterator
		while (vit.hasNext && mit.hasNext) {
			var v = vit.next.getTime
			if (!mit.next && v < min)
				min = v
		}
		return new Date(min)
	}
	def maxOf(values:Traversable[Date], missing:Traversable[Boolean] = null):Date = {
		if (missing == null) return new Date(values.map(_.getTime).max)
		var max = Long.MinValue
		var vit = values.toIterator
		var mit = missing.toIterator
		while (vit.hasNext && mit.hasNext) {
			var v = vit.next.getTime
			if (!mit.next && v > max)
				max = v
		}
		return new Date(max)
	}
}


abstract class AxisRenderer[T] {
	var inverse:Boolean = false
	def render(axis:Axis[T], g:Graphics2D, rect:IntRect, horizontal:Boolean)
	def renderText(axis:Axis[T], g:Graphics2D, rect:IntRect, horizontal:Boolean) = {}
}


class EmptyAxisRenderer extends LineAxisRenderer[Any] {}
class LineAxisRenderer[T] extends AxisRenderer[T] {
	
	val tapSize = 4
	
	def largeHTap(g:Graphics2D, rect:IntRect, x:Int) =
		if (inverse) 	g.drawLine(x, rect.yn, x, Math.max(rect.y0, rect.yn - tapSize))
		else 			g.drawLine(x, rect.y0, x, Math.min(rect.yn, rect.y0 + tapSize))
		
	def smallHTap(g:Graphics2D, rect:IntRect, x:Int) =
		if (inverse) 	g.drawLine(x, rect.y0, x, rect.y0 + 1)
		else			g.drawLine(x, rect.yn, x, rect.yn - 1)
	
	def largeVTap(g:Graphics2D, rect:IntRect, y:Int) =
		if (inverse) 	g.drawLine(rect.x0, y, Math.min(rect.xn, rect.x0 + tapSize), y)
		else			g.drawLine(rect.xn, y, Math.max(rect.x0, rect.xn - tapSize), y)
	
	def smallVTap(g:Graphics2D, rect:IntRect, y:Int) =
		if (inverse) 	g.drawLine(rect.x0, y, rect.x0 + 1, y)
		else			g.drawLine(rect.xn, y, rect.xn - 1, y)
		
	def render(axis:Axis[T], g:Graphics2D, rect:IntRect, horizontal:Boolean) = {
		if (rect.width > 0 && rect.height > 0) {
			var padX = (rect.width * axis.reversePad).toInt
			var padY = (rect.height * axis.reversePad).toInt
			if (horizontal) {
				var y = if (inverse) rect.yn else rect.y0
				g.drawLine(rect.x0, y, rect.xn, y)
				largeHTap(g, rect, rect.x0 + padX)
				largeHTap(g, rect, rect.xn - 1 - padX)
			} else {
				var x = if (inverse) rect.x0 else rect.xn
				g.drawLine(x, rect.y0, x, rect.yn)
				largeVTap(g, rect, rect.y0 + padY)
				largeVTap(g, rect, rect.yn - padY)
			}
		}
	}
}



class LinearAxisRenderer(
		var precision:Int = 2
) extends LineAxisRenderer[Double] {
	override def renderText(
			axis:Axis[Double], 
			g:Graphics2D, 
			rect:IntRect, 
			horizontal:Boolean
	) = {
		val precString = "%."+precision+"f"
		if (rect.width > 0 && rect.height > 0) {
			var min = 	precString.format(axis.min)
			var max = 	precString.format(axis.max)
			
			if (horizontal) {
				g.translate(rect.x0, rect.y0)
				g.drawString(min, 4, 12)
				g.drawString(max, rect.width - Strings.width(max, g.getFontMetrics) - 4, 12)
				g.translate(-rect.x0, -rect.y0)
			} else {
				g.rotate(-90.0.toRadians)
				g.translate(-rect.yn, rect.x0)
				g.drawString(min.toString, 4, 12)
				g.drawString(max, rect.height - Strings.width(max, g.getFontMetrics) - 4, 12)
				g.translate(rect.yn, -rect.x0)
				g.rotate(90.0.toRadians)
			}
		}
	}
}



class LogAxisRenderer(
		var precision:Int = 2
) extends LineAxisRenderer[Double] {
	override def render(
			axis:Axis[Double], 
			g:Graphics2D, 
			rect:IntRect, 
			horizontal:Boolean
	) = {
		super.render(axis, g, rect, horizontal)
		if (rect.width > 0 && rect.height > 0) {
			var lmin = math.log10(axis.min)
			var lmax = math.log10(axis.max)
			var li = math.ceil(lmin)
			while (li < lmax) {
				if (horizontal)
					largeHTap(g, rect, ((li - lmin) / (lmax - lmin) * rect.width).toInt)
				else
					largeVTap(g, rect, ((li - lmin) / (lmax - lmin) * rect.height).toInt)
				var half = math.log10(math.exp(li) * 5)
				if (half < lmax)
					if (horizontal)
						smallHTap(g, rect, ((half - lmin) / (lmax - lmin) * rect.width).toInt)
					else
						smallVTap(g, rect, ((half - lmin) / (lmax - lmin) * rect.height).toInt)
				li += 1
			}
		}
	}
	
	override def renderText(
			axis:Axis[Double], 
			g:Graphics2D, 
			rect:IntRect, 
			horizontal:Boolean
	) = {
		super.renderText(axis, g, rect, horizontal)
		if (rect.width > 0 && rect.height > 0) {
			val precString = "%."+precision+"e"
			var min = 	if (precision > 0) precString.format(axis.min)
						else				axis.min.toInt.toString
			var max = 	if (precision > 0) precString.format(axis.max)
						else				axis.max.toInt.toString
			
			if (horizontal) {
				g.translate(rect.x0, rect.y0)
				g.drawString(min, 4, 12)
				g.drawString(max, rect.width - Strings.width(max, g.getFontMetrics) - 4, 12)
				g.translate(-rect.x0, -rect.y0)
			} else {
				g.rotate(-90.0.toRadians)
				g.translate(-rect.yn, rect.x0)
				g.drawString(min.toString, 4, 12)
				g.drawString(max, rect.height - Strings.width(max, g.getFontMetrics) - 4, 12)
				g.translate(rect.yn, -rect.x0)
				g.rotate(90.0.toRadians)
			}
		}
	}
}



class DateAxisRenderer(
) extends LineAxisRenderer[Date] {
	override def renderText(
			axis:Axis[Date], 
			g:Graphics2D, 
			rect:IntRect, 
			horizontal:Boolean
	) = {
		if (rect.width > 0 && rect.height > 0) {
			var df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.UK)
			var min = df.format(axis.min)
			var max = df.format(axis.max)
			
			if (horizontal) {
				g.translate(rect.x0, rect.y0)
				g.drawString(min, 4, 12)
				g.drawString(max, rect.width - Strings.width(max, g.getFontMetrics) - 4, 12)
				g.translate(-rect.x0, -rect.y0)
			} else {
				g.rotate(-90.0.toRadians)
				g.translate(-rect.yn, rect.x0)
				g.drawString(min.toString, 4, 12)
				g.drawString(max, rect.height - Strings.width(max, g.getFontMetrics) - 4, 12)
				g.translate(rect.yn, -rect.x0)
				g.rotate(90.0.toRadians)
			}
		}
	}
}
