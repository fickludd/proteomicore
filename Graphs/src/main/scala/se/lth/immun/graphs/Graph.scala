package se.lth.immun.graphs

import java.util.Date
import java.awt.image.BufferedImage

import scala.swing._
import scala.swing.event._
import scala.collection.mutable.ArrayBuffer

import se.lth.immun.graphs.util._
import se.lth.immun.graphs.event.SelectedNewInterval
import se.lth.immun.graphs.event.ZoomChanged


class TimeLineGraph(
		title:String = "",
		yPrecision:Int = 2
) extends Graph[Date, Double](title) {
	var xAxis:Axis[Date] = new DateAxis(new Date, new Date)
	var yAxis:Axis[Double] = new LinearAxis(0.0, 1.0) { renderer = new LinearAxisRenderer(yPrecision) }

	var xMin = xAxis.min
	var xMax = xAxis.max
}



class LineGraph(
		title:String = "",
		xPrecision:Int = 2,
		yPrecision:Int = 2
) extends Graph[Double, Double](title) {
	var xAxis:Axis[Double] = new LinearAxis(0.0, 1.0) { renderer = new LinearAxisRenderer(xPrecision) }
	var yAxis:Axis[Double] = new LinearAxis(0.0, 1.0) { renderer = new LinearAxisRenderer(yPrecision) }

	var xMin = xAxis.min
	var xMax = xAxis.max
}



abstract class Graph[X, Y](
		var title:String = ""
) extends Component {
	
	var cacheRender = false
	var cache:BufferedImage = null
	var cacheDirty = true
	var renderer = new LineGraphRenderer[X, Y]
	var style:LineGraphStyle = new LineGraphStyle
	
	var curves:Seq[Curve2[X, Y]] = Nil
	var xAxis:Axis[X]
	var yAxis:Axis[Y]
	var xMin:X
	var xMax:X
	var annotations:List[Annotation[LineGraphRenderer[X, Y]]] = Nil
	
	def leftMouseButton(me:MouseEvent):Boolean = me.peer.getButton != java.awt.event.MouseEvent.BUTTON3
	def rightMouseButton(me:MouseEvent):Boolean = me.peer.getButton == java.awt.event.MouseEvent.BUTTON3
	
	var mouseZoom = true
	var dragMods:Key.Modifiers = _
	var zoomMods:Key.Modifiers = 1024 //Magic number for no mods
	var start:Option[X] = None
	var end:Option[X] = None
	var selection:Option[Interval[X]] = None
	listenTo(mouse.clicks)
	listenTo(mouse.moves)
	reactions += {
		case mp:MousePressed => if (leftMouseButton(mp)) {
			dragMods = mp.modifiers
			start = Some(renderer.px2x(mp.point.x))
			repaint
		}
		case mm:MouseDragged => if (leftMouseButton(mm) && start.isDefined) {
			end = Some(renderer.px2x(mm.point.x))
			repaint
		}
		case mr:MouseReleased => if (leftMouseButton(mr)) {
			end match {
				case Some(endX) => {
					var dx = xAxis.x2gx(endX) - xAxis.x2gx(start.get)
					if (math.abs(dx) > 0.02	&& mouseZoom && dragMods == zoomMods) {
						setZoom(start.get, endX)
						selection = None
						publish(new ZoomChanged[X](start.get, endX, this))
					} else {
						selection = Some(new Interval[X](start.get, endX))
						publish(new SelectedNewInterval[X](start.get, endX, this, dragMods))
					}
					start = None
					end = None
					repaint
				}
				case None => {}
			}
		}
		case mc:MouseClicked => {
			if (rightMouseButton(mc)) {
				if (mouseZoom) {
					setZoom(xMin, xMax)
					publish(new ZoomChanged[X](xMin, xMax, this))
				}
				selection = None
				repaint
			}
		}
	}
	
	
	
	def setCurves(
				curves:Seq[Curve2[X, Y]]
	):Boolean = {
		annotations = Nil
		cacheDirty = true
		if (curves.isEmpty || curves(0).xs.isEmpty) {
			clear
			return false
		} else {
			this.curves = curves
			var minMaxs = curves.map(c => (xAxis.minOf(c.xs), xAxis.maxOf(c.xs)))
			xMin = xAxis.minOf(minMaxs.map(_._1))
			xMax = xAxis.maxOf(minMaxs.map(_._2))
			setZoom(xMin, xMax)
			return true
		}
	}
	
	
	def clear() = {
		curves = Nil
		annotations = Nil
	}
	def clearSelection() = { selection = None; repaint }
	
	
	/*
	def setZoom(xs:Seq[X]):Unit = {
		if (xs.isEmpty) return
		xAxis.zoom(xs)
		var zoomedCurves = curves.map(_.filter(d => 
				xAxis.isVisible(d.x) && !d.isMissing
			))
		if (!zoomedCurves.isEmpty && zoomedCurves(0).size > 0)
			yAxis.zoom(zoomedCurves.flatMap(_.map(_.y)))
	}
	*/
	
	
	def setZoom(x0:X, xn:X):Unit = {
		xAxis.zoom(x0, xn)
		cacheDirty = true
		var zoomedCurves = curves.map(_.zoom(xAxis))
		if (!zoomedCurves.isEmpty && !zoomedCurves(0).xs.isEmpty) {
			var minMaxs = zoomedCurves.map(c => (yAxis.minOf(c.ys, c.missing), yAxis.maxOf(c.ys, c.missing)))
			var yMin = yAxis.minOf(minMaxs.map(_._1))
			var yMax = yAxis.maxOf(minMaxs.map(_._2))
			yAxis.zoom(yMin, yMax)
		}
	}
	
	
	def setZoom(x0:X, xn:X, y0:Y, yn:Y):Unit = {
		xAxis.zoom(x0, xn)
		yAxis.zoom(y0, yn)
	}
	
	
	
	def addAnnotation(a:Annotation[LineGraphRenderer[X, Y]]) = 
		annotations = a :: annotations
	
	
	
	def render(g:Graphics2D, renderer:LineGraphRenderer[X, Y]):Unit = {
		import Annotation._
		if (!cacheRender) {
			var ann = annotations
			if (start.isDefined)
				ann = new XAnnotation(start.get, ACTIVE) :: ann
			if (end.isDefined)
				ann = new XAnnotation(end.get, ACTIVE) :: ann
			if (selection.isDefined) {
				var i = selection.get
				ann = new XAnnotation(i.x0) :: new XAnnotation(i.xn) :: ann
			}
			
			_render(g, title, curves, ann)
		} else {
			if (cache == null || cacheDirty || size.width != cache.getWidth || size.height != cache.getHeight) {
				cache = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB)
				var g2 = cache.createGraphics
				_render(g2, title, curves, annotations.filter(!_.active))
				g2.dispose
				cacheDirty = false
			}
			g.drawImage(cache, 0, 0, null)
			var ann = annotations.filter(_.active).toBuffer
			if (start.isDefined)
				ann += new XAnnotation(start.get, ACTIVE)
			if (end.isDefined)
				ann += new XAnnotation(end.get, ACTIVE)
			if (selection.isDefined) {
				var i = selection.get
				ann += new XAnnotation(i.x0)
				ann += new XAnnotation(i.xn)
			}
			renderer.renderAnnotations(g, ann)
		}
	}
	
	def _render(
			g:Graphics2D, 
			title:String,
			curves:Seq[Curve2[X, Y]], 
			ann:Seq[Annotation[LineGraphRenderer[X, Y]]]
	) = {
		renderer.render(g, title, curves, ann)
	}
	
	
	def renderCurves(g:Graphics2D) = {
		renderer.renderBackgroundAnnotations(g, annotations)
		renderer.renderCurves(g, renderer.getZoomedCurves(curves))
		renderer.renderForegroundAnnotations(g, annotations)
	}
	
	
	override def paintComponent(g:Graphics2D) = {
		super.paintComponent(g)
		renderer.setup(xAxis, yAxis, style, new Size(size.width, size.height))
		render(g, renderer)
	}
}


trait OverlayGraph[X, Y, Y2] extends Graph[X, Y] {
	val overlayGraph:Graph[X, Y2]
	
	/*
	override def render(
			g:Graphics2D, 
			renderer:LineGraphRenderer[X, Y]
	):Unit = {
		super.render(g, renderer)
		var yAxisRect = new IntRect(
							renderer.size.width - renderer.yAxisWidth, 
							0,
							renderer.size.width, 
							renderer.size.height - renderer.xAxisHeight)
		var r = overlayGraph.yAxis.renderer
		r.inverse = true
		g.setColor(overlayGraph.style.curveColors(0))
		r.render(		overlayGraph.yAxis, g, yAxisRect, false)
		g.setColor(overlayGraph.style.annotColBackgroundText)
		r.renderText(	overlayGraph.yAxis, g, yAxisRect, false)
	}
	*/
	
	override def _render(
			g:Graphics2D, 
			title:String,
			curves:Seq[Curve2[X, Y]], 
			ann:Seq[Annotation[LineGraphRenderer[X, Y]]]
	) = {
		super._render(g, title, curves, ann)
		overlayGraph.renderCurves(g)
		
		var yAxisRect = new IntRect(
							renderer.size.width - renderer.yAxisWidth, 
							0,
							renderer.size.width, 
							renderer.size.height - renderer.xAxisHeight)
		var r = overlayGraph.yAxis.renderer
		r.inverse = true
		g.setColor(overlayGraph.style.curveColors(0))
		r.render(		overlayGraph.yAxis, g, yAxisRect, false)
		g.setColor(overlayGraph.style.annotColBackgroundText)
		r.renderText(	overlayGraph.yAxis, g, yAxisRect, false)
		
	}
	
	
	/*
	override def setZoom(xs:Seq[X]):Unit = {
		super.setZoom(xs)
		overlayGraph.setZoom(xs)
	}
	*/
	
	
	override def setZoom(x0:X, xn:X):Unit = {
		super.setZoom(x0, xn)
		overlayGraph.setZoom(x0, xn)
	}
	
	
	override def setZoom(x0:X, xn:X, y0:Y, yn:Y):Unit = {
		super.setZoom(x0, xn, y0, yn)
		overlayGraph.setZoom(x0, xn)
	}
	
	
	override def paintComponent(g:Graphics2D) = {
		super.paintComponent(g)
		renderer.setup(xAxis, yAxis, style, new Size(size.width, size.height))
		overlayGraph.renderer.setup(xAxis, overlayGraph.yAxis, overlayGraph.style, 
										new Size(size.width, size.height))
		render(g, renderer)
	}
}
