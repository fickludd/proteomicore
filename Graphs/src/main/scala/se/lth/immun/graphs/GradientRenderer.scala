package se.lth.immun.graphs

import scala.swing._
import scala.swing.event._
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import se.lth.immun.graphs.util._



class GradientRenderer[X]() extends LineGraphRenderer[X, Double] {

	var gradient:ColorTransform = new TwoPointGradientCT(Color.YELLOW, Color.BLACK)
	
	override def render(
			g:Graphics2D, 
			title:String,
			curves:Seq[CurveLike2[X, Double]], 
			annotations:Seq[Annotation[LineGraphRenderer[X, Double]]],
			renderActive:Boolean = true
	) = {
		yAxis.zoom(0.0, curves.length)
		super.render(g, title, curves, annotations, renderActive)
	}
	
	
	override def renderCurves(
			g:Graphics2D, 
			zoomedCurves:Seq[CurveLike2[X, Double]]
	) = {
		var dpy = (math.abs(y2py(1) - y2py(0)) / 2 - 1).toInt
		
		val axis0 = x2px(xAxis.min)
		val axisN = x2px(xAxis.max)
		
		for (i <- 0 until zoomedCurves.length) {
			var curve 	= zoomedCurves(i)
			var cx0 = x2px(curve.xs.head)
			var cxn = x2px(curve.xs.last)
			var bI 	= new BufferedImage(cxn - cx0, dpy*2, BufferedImage.TYPE_INT_RGB)
			var a 	= new Array[Int]((cxn - cx0) * dpy*2)
			var py 		= y2py(i)
			var lastPx = 0
			var nextPx = 0
			var lastY	= curve.ys.head
			var y = 0.0
			var k = 0.0
			var nextY = 0.0
			var min		= curve.ys.min
			var max		= curve.ys.max
			
			g.setColor(style.annotColPassive)
			g.drawLine(axis0, py, cx0, py)
			
			var line = new Array[Int](cxn - cx0)
			line(0) = gradient.getColor((lastY-min) / (max-min)).getRGB
			
			try {
				for (i <- 1 until curve.ys.length) {
					nextPx 	= x2px(curve.xs(i)) - cx0
					nextY 	= curve.ys(i)
					for (px <- lastPx until nextPx) {
						k = (px - lastPx) / (nextPx - lastPx).toDouble
						y = lastY * (1-k) + nextY * k
						line(px) = gradient.getColor((y-min) / (max-min)).getRGB
					}
					lastPx = nextPx
					lastY = nextY
				}
			} catch {
				case e:Exception => e.printStackTrace
			}
				
			for (y <- 0 until 2*dpy)
				for (x <- 0 until (cxn - cx0))
					a(y*(cxn - cx0) + x) = line(x)
			
			bI.setRGB(0, 0, (cxn - cx0), 2*dpy, a, 0, (cxn - cx0))
			g.drawImage(bI, null, cx0, py - dpy)
			
			g.setColor(style.annotColPassive)
			g.drawLine(cxn, py, axisN, py)
		}
	}
	
	
	override def renderAxis(g:Graphics2D) = {
		g.setColor(style.annotColBackground)
		xAxis.renderer.render(xAxis, g, xAxisRect, true)
		
		g.setColor(style.annotColBackgroundText)
		xAxis.renderer.renderText(xAxis, g, xAxisRect, true)
	}
	
	
	override def renderLegend(
			g:Graphics2D, 
			zoomedCurves:Seq[CurveLike2[X, Double]]
	) = {
		for (i <- 0 until zoomedCurves.length) {
			var curve 	= zoomedCurves(i)
			var col 	= style.curveColors(i % style.curveColors.length)
			var py 		= y2py(i)
			
			g.setColor(style.annotColPassive)
			g.drawString(curve.name, inner.x0-yAxisWidth, py)
		}
	}
}