package se.lth.immun.graphs

import scala.swing._
import scala.swing.event._
import java.awt.Color
import java.awt.Graphics2D
import se.lth.immun.graphs.util._



class RelativeRenderer[X]() extends LineGraphRenderer[X, Double] {

	
	override def render(
			g:Graphics2D, 
			title:String,
			curves:Seq[CurveLike2[X, Double]], 
			annotations:Seq[Annotation[LineGraphRenderer[X, Double]]],
			renderActive:Boolean = true
	) = {
		yAxis.zoom(0.0, 1.0)
		super.render(g, title, curves, annotations, renderActive)
	}
	
	
	override def renderCurves(
			g:Graphics2D, 
			zoomedCurves:Seq[CurveLike2[X, Double]]
	) = {
		
		var xs = zoomedCurves(0).xs.map(x => xAxis.x2gx(x))
		var ics = zoomedCurves.map(c => 
			c.ys.zip(c.missing).map(t => if (t._2) 0.0 else t._1)
		)
		var tot = ics.transpose.map(_.sum)
		var rels = ics.map(ys => 
			ys.zip(tot).map(t => t._1 / t._2)
		).transpose
		var missing = rels.zip(tot).map(t => t._1.exists(_ == 0.0) || t._2 == 0.0)
		
		for (i <- 0 until rels.length) {
			var r = rels(i)
			newRenderLine(
					(if (i == 0) xs(i) else xs(i-1)),
					xs(i),
					(if (i == rels.length - 1) xs(i) else xs(i+1))
				)
			
			if (!missing(i))
				for (j <- 0 until r.length) {
					var col = style.curveColors(j % style.curveColors.length)
					renderRelativeDataPoint(g, r(j), col)
				}
			else
				renderRelativeDataPoint(g, 1.0, style.annotColBackground)
		}
	}
	
	
	var _x0 	= 0
	var _xn 	= 0
	var _y		= 0.0
	def newRenderLine(prevX:Double, currX:Double, nextX:Double) = {
		_x0 = gx2px((prevX+currX)/2)
		_xn = gx2px((currX+nextX)/2)
		_y 	= 0.0
	}
	
	def renderRelativeDataPoint(
			g:Graphics2D, 
			dy:Double, 
			col:Color
	) = {
		g.setColor(col)
		var y0 = y2py(math.min(1.0, _y + dy))
		var yn = y2py(_y)
		g.fillRect(_x0, y0, _xn-_x0, yn-y0)
		//g.drawLine(d.x, gy2py(_y), d.x, gy2py(_y + d.y))
		_y += dy
	}
}