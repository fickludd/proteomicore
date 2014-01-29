package se.lth.immun.graphs

import scala.swing._
import scala.swing.event._
import se.lth.immun.graphs.util.Strings;

import java.awt.Color
import java.awt.Graphics2D

import se.lth.immun.graphs.util._



class LineGraphStyle {
	/*
	 * Color taken from d3js category20
	 */
	var curveColors:Seq[Color] = Array(
			new Color(0x1f77b4), new Color(0xaec7e8), 
			new Color(0xff7f0e), new Color(0xffbb78), 
			new Color(0x2ca02c), new Color(0x98df8a), 
			new Color(0xd62728), new Color(0xff9896), 
			new Color(0x9467bd), new Color(0xc5b0d5), 
			new Color(0x8c564b), new Color(0xc49c94), 
			new Color(0xe377c2), new Color(0xf7b6d2), 
			new Color(0x7f7f7f), new Color(0xc7c7c7), 
			new Color(0xbcbd22), new Color(0xdbdb8d), 
			new Color(0x17becf), new Color(0x9edae5))
	
	var backgroundColor = Color.WHITE
	
	var annotColActive = Color.RED
	var annotColPassive = Color.BLACK
	var annotColBackground = Color.GRAY
	var annotColBackgroundText = Color.GRAY
	
	
	var dataPointSymbol = ' '
	var dataPointSymbolSize = 2
}



class LineGraphRenderer[X, Y]() extends XConverter[X] with YConverter[Y] {
	
	var style:LineGraphStyle = null
	val xAxisHeight = 16
	val yAxisWidth = 16
	
	var xAxisRect:IntRect = null
	var yAxisRect:IntRect = null
	
	def annotationColor(a:Annotation[LineGraphRenderer[X, Y]]):Color = 
		a.col match {
			case c:Annotation.Color => {
				if (c.active) style.annotColActive
				else if (c.background) style.annotColBackground
				else style.annotColPassive
			}
			case c:Color => c
			case _ => Color.BLACK
		}
	
	
	def setup(
			xAxis:Axis[X],
			yAxis:Axis[Y], 
			style:LineGraphStyle, 
			size:Size
	) = {
		this.xAxis = xAxis
		this.yAxis = yAxis
		this.style = style
		this.size = size
		inner = new IntRect(
								yAxisWidth, 
								0, 
								size.width, 
								size.height - xAxisHeight
							)
		xAxisRect = new IntRect(
								yAxisWidth, 
								size.height - xAxisHeight, 
								size.width, 
								size.height
							)
		yAxisRect = new IntRect(
								0, 
								0, 
								yAxisWidth, 
								size.height - xAxisHeight
							)
	}
	
	
	def getZoomedCurves(
			curves:Seq[CurveLike2[X, Y]]
	):Seq[CurveLike2[X, Y]] = {
		/*
		 def bin(
				curve:Curve2[X, Y]
		):Curve2[X, Y] = {
			var start = 0
			(0 until inner.width).map(i => {
				var binx = inner.x0 + i
				var end = curve.xs.indexWhere(x => x2px(x) >= (binx + 1), start)
				var dp = if (end > start) {
					var bin = curve.ys.slice(start, end)
					new DataPoint[X, Y](curve.xs(start), )
				}
				start = end
			}).withFilter(!_.isEmpty).map(binned => {
				new DataPoint[X, Y](
						xAxis.minOf(binned.map(_.x)), 
						yAxis.maxOf(binned.map(_.y))
					)
			})
		}
		*/
		var zoomed = curves.map(_.zoom(xAxis))
		//if (!zoomed.isEmpty && zoomed(0).xs.size > inner.width)
		//	zoomed.map(curve => new Curve(bin(curve), curve.name, curve.col))
		//else
		zoomed
	}
	
	
	def render(
			g:Graphics2D, 
			title:String,
			curves:Seq[CurveLike2[X, Y]], 
			annotations:Seq[Annotation[LineGraphRenderer[X, Y]]],
			renderActive:Boolean = true
	) = {
		renderBackground(g)
		renderBackgroundAnnotations(g, annotations)
		
		var zoomedCurves = getZoomedCurves(curves)
		renderCurves(g, zoomedCurves)
		
		renderForegroundAnnotations(g, annotations)
		if (renderActive) renderActiveAnnotations(g, annotations)
		renderAxis(g)
		renderLegend(g, zoomedCurves)
		renderTitle(g, title)
	}
	
	
	def renderBackground(g:Graphics2D) = {
		g.setColor(style.backgroundColor)
		g.fillRect(0, 0, size.width, size.height)
	}
	
	def renderBackgroundAnnotations(
			g:Graphics2D, 
			annotations:Seq[Annotation[LineGraphRenderer[X, Y]]]
	) = {
		for (la <- annotations.filter(_.background))
			la.render(g, this, style.annotColBackground)
	}
	
	def renderForegroundAnnotations(
			g:Graphics2D, 
			annotations:Seq[Annotation[LineGraphRenderer[X, Y]]]
	) = {
		for (la <- annotations.filter(a => !a.background && !a.active))
			la.render(g, this, annotationColor(la))
	}
	
	def renderActiveAnnotations(
			g:Graphics2D, 
			annotations:Seq[Annotation[LineGraphRenderer[X, Y]]]
	) = {
		for (la <- annotations.filter(a => !a.background && a.active))
			la.render(g, this, annotationColor(la))
	}
	
	def renderAnnotations(
			g:Graphics2D, 
			annotations:Seq[Annotation[LineGraphRenderer[X, Y]]]
	) = {
		for (a <- annotations)
			a.render(g, this, annotationColor(a))
	}
	
	def renderCurves(
			g:Graphics2D, 
			zoomedCurves:Seq[CurveLike2[X, Y]]
	) = {
		
		for (i <- 0 until zoomedCurves.length) {
			var curve 	= zoomedCurves(i)
			var l 		= curve.xs.size
			var pxs 	= new Array[Int](l)
			var pys 	= new Array[Int](l)
			var col 	= 	if (curve.color != null) curve.color 
							else style.curveColors(i % style.curveColors.length)
			
			for (j <- 0 until l) {
				pxs(j) = x2px(curve.xs(j))
				pys(j) = if (!curve.missing(j)) y2py(curve.ys(j)) else 1
				renderDataPoint(g, pxs(j), pys(j), curve.missing(j), col)
				
				if (j > 0) {
					if (!curve.missing(j-1) && !curve.missing(j)) {
						g.setColor(col)
						g.drawLine(pxs(j-1), pys(j-1), pxs(j), pys(j))
					}
				}
			}
			
			
		}
	}
	
	def renderAxis(g:Graphics2D) = {
		g.setColor(style.annotColBackground)
		xAxis.renderer.render(xAxis, g, xAxisRect, true)
		yAxis.renderer.render(yAxis, g, yAxisRect, false)
		
		g.setColor(style.annotColBackgroundText)
		xAxis.renderer.renderText(xAxis, g, xAxisRect, true)
		yAxis.renderer.renderText(yAxis, g, yAxisRect, false)
	}
	
	def renderLegend(
			g:Graphics2D, 
			zoomedCurves:Seq[CurveLike2[X, Y]]
	) = {
		var y = inner.y0 + 10
		var i = 0
		for (zc <- zoomedCurves.filter(c => c.name != null && c.name != "")) {
			var col 	= 	if (zc.color != null) zc.color 
							else style.curveColors(i % style.curveColors.length)
			g.setColor(col)
			g.drawLine(inner.x0 + 4, y, inner.x0 + 14, y)
			g.setColor(style.annotColPassive)
			g.drawString(zc.name, inner.x0 + 17, y + 6)
			y += 12
			i += 1
		}
	}
	
	def renderTitle(
			g:Graphics2D, 
			title:String
	) = {
		g.setColor(style.annotColPassive)
		g.drawString(title, inner.xn - Strings.width(title, g.getFontMetrics) - 4, 12)
	}
	
	
	def renderDataPoint(
			g:Graphics2D, 
			px:Int,
			py:Int,
			missing:Boolean,
			col:Color
	) = {
		var s = style.dataPointSymbolSize
		if (missing) {
			g.setColor(style.annotColBackground)
			var mpy = gy2py(0.5)
			g.drawLine(px - s, mpy - s, px + s, mpy + s)
			g.drawLine(px - s, mpy + s, px + s, mpy - s)
		} else {
			g.setColor(col)
			style.dataPointSymbol match {
				case 'x' => {
					g.drawLine(px - s, 	py - s, 	px + s, 	py + s	)
					g.drawLine(px - s, 	py + s, 	px + s, 	py - s	)
				}
				case 'o' => {
					g.drawOval(px - s, 	py - s, 	2*s + 1, 	2*s + 1	)
				}
				case '+' => {
					g.drawLine(px - s, 	py, 		px + s, 	py		)
					g.drawLine(px, 		py + s, 	px, 		py - s	)
				}
				case '.' => {
					g.setColor(Color.WHITE)
					g.drawLine(px, py, px, py+1)
				}
				case _ => {}
			}
		}
	}
}