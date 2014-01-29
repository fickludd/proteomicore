package se.lth.immun.graphs


import scala.swing._
import scala.swing.event._
import scala.collection.mutable.ArrayBuffer


import java.awt.Color
import java.awt.Paint
import java.awt.TexturePaint
import java.awt.Rectangle
import java.awt.image.BufferedImage
import se.lth.immun.graphs.util._




class OnOffSlot[X](
		var start:X,
		var end:X,
		var mid:Option[X] = None,
		var group:Int = -1
) {}




class OnOffCurve[X](
		var slots:ArrayBuffer[OnOffSlot[X]],
		var name:String,
		var id1:Int,
		var id2:Int
) {
	def zoom(xAxis:Axis[X]) = {
		
		var newSlots = new ArrayBuffer[OnOffSlot[X]]
		var minGx = xAxis.x2gx(xAxis.min)
		var maxGx = xAxis.x2gx(xAxis.max)
		
		
		def cut(s:OnOffSlot[X]):OnOffSlot[X] = {
			var ret = s
			if (xAxis.x2gx(ret.start) < minGx)
				ret = new OnOffSlot(xAxis.min, ret.end)
			if (xAxis.x2gx(ret.end) > maxGx)
				ret = new OnOffSlot(ret.end, xAxis.max)
			s.mid match {
				case Some(x) => {
					var midGx = xAxis.x2gx(x)
					if (midGx < minGx || midGx > maxGx)
						ret.mid = None
					else
						ret.mid = s.mid
				}
				case None => {}
			}
			ret.group = s.group
			ret
		}
		
		
		val s1 = slots.dropWhile(s => xAxis.x2gx(s.end) < minGx)
		var s2 = s1.takeWhile(s => xAxis.x2gx(s.start) < maxGx)
		if (!s2.isEmpty) {
			s2 = cut(s2.head) +: s2.tail
			s2 = s2.init :+ cut(s2.last)
		}
		new OnOffCurve(s2, name, id1, id2)
	}
}




class OnOffSlotConnector[X](
		var curve1:Int,
		var x1:X,
		var curve2:Int,
		var x2:X,
		var active:Boolean = false
) {}




abstract class OnOffGraph[X](
		var title:String = ""
) extends Component {

	var cacheRender = false
	var cache:BufferedImage = null
	var cacheDirty = true
	var renderer 	= new OnOffRenderer[X]
	var style 		= new LineGraphStyle
	var connectorVisZoomFactor = 0.1
	
	var curves:Seq[OnOffCurve[X]] 	= Nil
	var connectors 					= new ArrayBuffer[OnOffSlotConnector[X]]
	var xAxis:Axis[X]
	var xMin:X
	var xMax:X
	
	
	
	def setCurves(
				curves:Seq[OnOffCurve[X]],
				xMin:X,
				xMax:X
	):Boolean = {
		connectors.clear
		cacheDirty = true
		this.xMin = xMin
		this.xMax = xMax
		if (curves.isEmpty) {
			clear
			return false
		} else {
			this.curves = curves
			return true
		}
	}
	
	
	def clear() = {
		curves = Nil
		connectors.clear
	}
	
	
	def setZoom(x0:X, xn:X):Unit = {
		xAxis.zoom(x0, xn)
		cacheDirty = true
	}
	
	
	
	def render(g:Graphics2D, renderer:OnOffRenderer[X]):Unit = {
		/*var ann:Seq[Annotation] = Nil
		import AnnotationColor._
		if (start.isDefined)
			con = new XAnnotation(start.get, Active) :: ann
		if (end.isDefined)
			ann = new XAnnotation(end.get, Active) :: ann
		if (selection.isDefined) {
			var i = selection.get
			ann = new XAnnotation(i.x0, Passive) :: new XAnnotation(i.xn, Passive) :: ann
		}*/
		if (!cacheRender) {
			var mag = xAxis.magnification(xMin, xMax)
			renderer.connectorColor = new Color(1.0f, 1.0f, 1.0f, (0.1 * (1/mag) + 1.0f * (1-1/mag)).toFloat)
			renderer.render(g, title, curves, connectors)
		} else {
			if (cache == null || cacheDirty || size.width != cache.getWidth || size.height != cache.getHeight) {
				var mag = xAxis.magnification(xMin, xMax)
				renderer.connectorColor = new Color(1.0f, 1.0f, 1.0f, (0.1 * (1/mag) + 1.0f * (1-1/mag)).toFloat)
				cache = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB)
				var g2 = cache.createGraphics
				renderer.render(g2, title, curves, connectors.filter(!_.active))
				g2.dispose
				cacheDirty = false
			}
			g.drawImage(cache, 0, 0, null)
			renderer.renderConnectors(g, connectors.filter(_.active))
		}
	}
	
	
	def renderCurves(g:Graphics2D) = {
		//renderer.renderBackgroundAnnotations(g, annotations)
		renderer.renderCurves(g, renderer.getZoomedCurves(curves))
		//renderer.renderForegroundAnnotations(g, annotations)
	}
	
	
	override def paintComponent(g:Graphics2D) = {
		super.paintComponent(g)
		renderer.setup(xAxis, style, new Size(size.width, size.height))
		render(g, renderer)
	}
}



class OnOffRenderer[X] extends XConverter[X] with YConverter[Double] {
	
	var connectorColor = new Color(1.0f, 1.0f, 1.0f, 0.5f)
	var style:LineGraphStyle = null
	var lightBGCol = Color.WHITE
	val xAxisHeight = 16
	val yAxisWidth = 16
	
	var xAxisRect:IntRect = null
	var yAxisRect:IntRect = null
	
	var paints:Array[Array[Paint]] = null
	
	
	def setup(
			xAxis:Axis[X],
			style:LineGraphStyle, 
			size:Size
	) = {
		this.xAxis = xAxis
		this.yAxis = new LinearAxis(0, 1)
		this.style = style
		lightBGCol = style.backgroundColor.brighter
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
		
		
		var l = style.curveColors.length
		paints = new Array[Array[Paint]](l)
		for (i <- 0 until l) {
			paints(i) = new Array[Paint](l)
			for (j <- 0 until l) {
				var bi 		= new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB)
				var col1 	= style.curveColors(i).getRGB
				var col2 	= style.curveColors(j).getRGB
				
				var a = Array(
							col1, col2, col2, col1, 
							col2, col2, col1, col1, 
							col2, col1, col1, col2, 
							col1, col1, col2, col2
						)
				bi.setRGB(0, 0, 4, 4, a, 0, 4)
				paints(i)(j) = new TexturePaint(bi, new Rectangle(0, 0, 4, 4))
			}
		}
	}
	
	
	def render(
			g:Graphics2D, 
			title:String,
			curves:Seq[OnOffCurve[X]], 
			connectors:Seq[OnOffSlotConnector[X]]
	) = {
		renderBackground(g)
		
		yAxis.zoom(0, curves.length)
		var zoomedCurves = getZoomedCurves(curves)
		renderCurves(g, zoomedCurves)
		renderConnectors(g, connectors)
		
		renderAxis(g)
		renderLegend(g, zoomedCurves)
		renderTitle(g, title)
	}
	
	
	def renderBackground(g:Graphics2D) = {
		g.setColor(style.backgroundColor)
		g.fillRect(0, 0, size.width, size.height)
	}
	
	
	def getZoomedCurves(
			curves:Seq[OnOffCurve[X]]
	):Seq[OnOffCurve[X]] = {
		var zoomed = curves.map(_.zoom(xAxis))
		zoomed
	}
	
	
	def renderCurves(
			g:Graphics2D, 
			zoomedCurves:Seq[OnOffCurve[X]]
	) = {
		var dpy = (math.abs(y2py(1) - y2py(0)) / 2 - 1).toInt
		
		for (i <- 0 until zoomedCurves.length) {
			var curve 	= zoomedCurves(i)
			var l		= style.curveColors.length
			var paint = paints(curve.id1 % l)(curve.id2 % l)
			//var col1 	= style.curveColors(curve.id1 % style.curveColors.length)
			//var col2 	= style.curveColors(curve.id2 % style.curveColors.length)
			var py 		= y2py(i)
			
			var lastPx = x2px(xAxis.min)
			for (s <- curve.slots) {
				var px1 = x2px(s.start)
				var px2 = x2px(s.end)
				
				g.setColor(style.annotColPassive)
				g.drawLine(lastPx, py, px1, py)
				
				/*
				g.setPaint(paint)
				g.fillRect(px1, py - dpy, px2 - px1, dpy*2)
				*/
				var col = lightBGCol
				if (s.group != -1)
					col = style.curveColors(s.group % style.curveColors.length)
					
				g.setColor(col)
				g.fillRect(px1, py - dpy, px2 - px1, dpy*2)
				
				g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.5f))
				g.drawRect(px1, py - dpy, px2 - px1, dpy*2)
				/*
				g.setColor(col1)
				g.fillRect(px1, py - dpy, 	px2 - px1, dpy)
				g.setColor(col2)
				g.fillRect(px1, py, 		px2 - px1, dpy)
				*/
				
				s.mid match {
					case Some(midx) => {
						g.setColor(style.annotColPassive)
						var mpx = x2px(midx)
						g.drawLine(mpx, py - dpy, mpx, py + dpy)
					}
					case None => {}
				}
				
				lastPx = px2
			}
			g.setColor(style.annotColPassive)
			g.drawLine(lastPx, py, x2px(xAxis.max), py)
		}
	}
	
	
	def renderConnectors(
			g:Graphics2D, 
			connectors:Seq[OnOffSlotConnector[X]]
	) = {
		for (c <- connectors) {
			g.setColor(if (c.active) style.annotColActive else connectorColor)
			var py1 	= y2py(c.curve1)
			var px1 	= x2px(c.x1)
			var py2 	= y2py(c.curve2)
			var px2 	= x2px(c.x2)
			g.drawLine(px1, py1, px2, py2)
			g.setColor(Color.WHITE)
			g.drawLine(px1, py1, px1+1, py1)
			g.drawLine(px2, py2, px2+1, py2)
		}
	}
	
	
	def renderAxis(g:Graphics2D) = {
		g.setColor(style.annotColBackground)
		xAxis.renderer.render(xAxis, g, xAxisRect, true)
		//yAxis.renderer.render(yAxis, g, yAxisRect, false)
		
		g.setColor(style.annotColBackgroundText)
		xAxis.renderer.renderText(xAxis, g, xAxisRect, true)
		//yAxis.renderer.renderText(yAxis, g, yAxisRect, false)
	}
	
	
	def renderLegend(
			g:Graphics2D, 
			zoomedCurves:Seq[OnOffCurve[X]]
	) = {
		for (i <- 0 until zoomedCurves.length) {
			var curve 	= zoomedCurves(i)
			var col 	= style.curveColors(i % style.curveColors.length)
			var py 		= y2py(i)
			
			g.setColor(style.annotColPassive)
			g.drawString(curve.name, inner.x0-yAxisWidth, py)
		}
	}
	
	
	def renderTitle(
			g:Graphics2D, 
			title:String
	) = {
		g.setColor(style.annotColPassive)
		g.drawString(title, inner.xn - Strings.width(title, g.getFontMetrics) - 4, 12)
	}
}