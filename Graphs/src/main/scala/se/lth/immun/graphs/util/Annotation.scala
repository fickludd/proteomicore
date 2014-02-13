package se.lth.immun.graphs.util

import se.lth.immun.graphs.GraphConverter
import se.lth.immun.graphs.XConverter
import se.lth.immun.graphs.YConverter
import java.awt.Graphics2D
import java.awt.Color

/*
object AnnotationColor extends Enumeration {

	type AnnotationColor = Value
	val Active, Passive, Background = Value
}
*/
object Annotation {
	val ACTIVE 				= new Color(true, false)
	val BACKGROUND 			= new Color(false, true)
	val ACTIVE_BACKGROUND 	= new Color(true, true)
	class Color(var active:Boolean, var background:Boolean) extends java.awt.Color(0)
}


abstract class Annotation[-C](
		var col:Color = null
) {
	var background 	= false
	var active		= false
	
	col match {
		case c:Annotation.Color => {
			active 		= c.active
			background 	= c.background
		}
		case _ => {}
	}
	def render(g:Graphics2D, c:C, col:Color):Unit
}


class LineAnnotation[X, Y](
		var x1:X,
		var y1:Y,
		var x2:X,
		var y2:Y,
		col:Color = null
) extends Annotation[XConverter[X] with YConverter[Y]](col) {
	
	override def render(g:Graphics2D, c:XConverter[X] with YConverter[Y], col:Color):Unit = {
		g.setColor(col)
		g.drawLine(c.x2px(x1), c.y2py(y1), c.x2px(x2), c.y2py(y2))
	}
}


class BoxAnnotation[X, Y](
		var x1:X,
		var y1:Y,
		var x2:X,
		var y2:Y,
		col:Color = null
) extends Annotation[XConverter[X] with YConverter[Y]](col) {
	
	override def render(g:Graphics2D, c:XConverter[X] with YConverter[Y], col:Color):Unit = {
		g.setColor(col)
		var x0 = Math.min(c.x2px(x1), c.x2px(x2))
		var y0 = Math.min(c.y2py(y1), c.y2py(y2))
		var dx = Math.abs(c.x2px(x2) - c.x2px(x1))
		var dy = Math.abs(c.y2py(y2) - c.y2py(y1))
		g.drawRect(x0, y0, dx, dy)
	}
}


class HeightBoxAnnotation[X, Y](
		var x1:X,
		var y1:Y,
		var x2:X,
		col:Color = null,
		var text:String = null
) extends Annotation[XConverter[X] with YConverter[Y]](col) {
	
	override def render(g:Graphics2D, c:XConverter[X] with YConverter[Y], col:Color):Unit = {
		g.setColor(col)
		var x0 = Math.min(c.x2px(x1), c.x2px(x2))
		var y0 = Math.min(c.y2py(y1), c.gy2py(0.0))
		var dx = Math.abs(c.x2px(x2) - c.x2px(x1))
		var dy = Math.abs(c.gy2py(0.0) - c.y2py(y1))
		g.drawRect(x0, y0, dx, dy)
		
		if (text != null && text != "") {
			g.translate(x0, y0)
			g.drawString(text, 4, if (y0 < 12) 12 else -3)
			g.translate(-x0, -y0)
		}
	}
}


class XAnnotation[X](
		var x:X,
		col:Color = null,
		var text:String = null
) extends Annotation[XConverter[X]](col) {
	
	override def render(g:Graphics2D, c:XConverter[X], col:Color):Unit = {
		g.setColor(col)
		var px = c.x2px(x)
		g.drawLine(px, c.inner.y0, px, c.inner.yn)
		if (text != null) {
			g.rotate(-90.0.toRadians)
			g.translate(-c.inner.height, px)
			g.drawString(text, 4, 12)
			g.translate(c.inner.height, -px)
			g.rotate(90.0.toRadians)
		}
	}
}


class YAnnotation[Y](
		var y:Y,
		col:Color = null,
		var text:String = null
) extends Annotation[YConverter[Y]](col) {
	
	override def render(g:Graphics2D, c:YConverter[Y], col:Color):Unit = {
		g.setColor(col)
		var py = c.y2py(y)
		g.drawLine(c.inner.x0, py, c.inner.xn, py)
		if (text != null) {
			g.translate(0, py)
			g.drawString(text, 4, 12)
			g.translate(0, -py)
		}
	}
}


class GXAnnotation(
		var gx:Double,
		col:Color = null,
		var text:String = null
) extends Annotation[GraphConverter](col) {
	
	override def render(g:Graphics2D, c:GraphConverter, col:Color):Unit = {
		g.setColor(col)
		var px = c.gx2px(gx)
		g.drawLine(px, c.inner.y0, px, c.inner.yn)
		if (text != null) {
			g.rotate(-90.0.toRadians)
			g.translate(-c.inner.height, px)
			g.drawString(text, 4, 12)
			g.translate(c.inner.height, -px)
			g.rotate(90.0.toRadians)
		}
	}
}


class GYAnnotation(
		var gy:Double,
		col:Color = null,
		var text:String = null
) extends Annotation[GraphConverter](col) {
	
	override def render(g:Graphics2D, c:GraphConverter, col:Color):Unit = {
		g.setColor(col)
		var py = c.gy2py(gy)
		g.drawLine(c.inner.x0, py, c.inner.xn, py)
		if (text != null) {
			g.translate(0, py)
			g.drawString(text, 4, 12)
			g.translate(0, -py)
		}
	}
}
