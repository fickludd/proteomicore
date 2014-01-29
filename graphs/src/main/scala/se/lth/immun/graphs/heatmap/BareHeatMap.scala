package se.lth.immun.graphs.heatmap

import swing._
import swing.event._
import se.lth.immun.collection.DataMatrix
import se.lth.immun.collection.AbstractAnyTree
import org.apache.commons.math3.stat.StatUtils

class BareHeatMap[R, C, D](
		var data:DataMatrix[R, C, D],
		var params:HeatMapParams[D]
) extends Component {
	type HMTree = AbstractAnyTree#AbstractAnyNode

	
	
	def setData(newData:DataMatrix[R, C, D]) = {
		data = newData
		globalRange 	= calculateGlobalRange
		rowLeaves 		= data.rowGrouping.leaves
		colLeaves 		= data.columnGrouping.leaves
		nColumns 		= colLeaves.length
		nRows 			= rowLeaves.length
		rowDelims 		= getDelims(data.rowGrouping)
		colDelims 		= getDelims(data.columnGrouping)
		pxWidth 		= colLeaves.length * params.tileWidth + colDelims.sum
		pxHeight 		= rowLeaves.length * params.tileHeight + rowDelims.sum
		minimumSize 	= new Dimension(pxWidth, pxHeight)
		maximumSize 	= new Dimension(pxWidth, pxHeight)
		preferredSize 	= new Dimension(pxWidth, pxHeight)
		highlighted	.reset(nColumns, nRows)
		selected	.reset(nColumns, nRows)
		repaint
	}
	
	
	/* data stats */
	private var toDouble	:(D => Double) 		= null
	private var filter		:(D => Boolean) 	= null
	private var transform	:(Double => Double) = null
	object Range { val EMPTY = new Range(0.0, 0.0, 0.0) }
	class Range(var min:Double, var mid:Double, var max:Double) {
		override def toString = "range %.2f, %.2f, %.2f".format(min, mid, max)
	}
	var globalRange:Range = calculateGlobalRange
	private def paramsDirty:Boolean = (
			toDouble 	!= params.toDouble 		||
			filter 		!= params.filter 		||
			transform 	!= params.transform
		)
	private def calculateGlobalRange = {
		//println("Recalculating global range")
		var max 	= data.max(params.toDouble, params.filter, params.transform)
		var min 	= data.min(params.toDouble, params.filter, params.transform)
		var mean 	= data.mean(params.toDouble, params.filter, params.transform)
		params.dataMin 	= min
		params.dataMid 	= mean
		params.dataMax 	= max
		toDouble 		= params.toDouble
		filter 			= params.filter
		transform 		= params.transform
		new Range(min, mean, max)
	}
	
	
	
	
	/* heatmap stats */
	var rowLeaves 				= data.rowGrouping.leaves
	var colLeaves 				= data.columnGrouping.leaves
	var nColumns:Int 			= colLeaves.length
	var nRows:Int 				= rowLeaves.length
	var rowDelims:Array[Int] 	= getDelims(data.rowGrouping)
	var colDelims:Array[Int] 	= getDelims(data.columnGrouping)
	var pxWidth 	= colLeaves.length * params.tileWidth + colDelims.sum
	var pxHeight 	= rowLeaves.length * params.tileHeight + rowDelims.sum
	minimumSize 	= new Dimension(pxWidth, pxHeight)
	maximumSize 	= new Dimension(pxWidth, pxHeight)
	preferredSize 	= new Dimension(pxWidth, pxHeight)
	//size = new Dimension(pxWidth, pxHeight)
	
	
	
	
	/* interaction */
	var highlighted 	= new MatrixSelection(this, nColumns, nRows)
	var selected 		= new MatrixSelection(this, nColumns, nRows)
	
	listenTo(mouse.moves)
	listenTo(mouse.clicks)
	reactions += {
		case mm:MouseMoved => {
				highlighted.select(toMatrixPosition(mm.point))
				if (highlighted.changed) repaint
			}
		case mc:MouseClicked => {
				selected.select(highlighted.pos)
				if (selected.changed) repaint
			}
	}
	
	
	
	
	/* interaction functions */
	private def highlight(pos:MatrixPosition) =
		highlighted.select(if (pos == selected.pos) MatrixPosition.INVALID else pos)
	private def safeSelect(pos:MatrixPosition) =
		if (pos.isValid(nColumns, nRows)) {
			selected.select(pos)
			repaint
		}
	def selectedColumn:Option[C] = if (selected.isValid)
							data.getColumnHeader(
								colLeaves(selected.pos.column).obj.asInstanceOf[C]
							)
						else None
	def selectedRow:Option[R] = if (selected.isValid)
							data.getRowHeader(
								rowLeaves(selected.pos.row).obj.asInstanceOf[R]
							)
						else None
	def selectLeft = 	safeSelect(selected.pos.left)
	def selectRight = 	safeSelect(selected.pos.right)
	def selectUp = 		safeSelect(selected.pos.up)
	def selectDown = 	safeSelect(selected.pos.down)
	def selection():Option[D] = {
		if (selected.isValid) {
			Some(get(
					selected.pos.row,
					selected.pos.column
					))
		} else
			return None
	}
	
	
	
	
	/* transformation functions */
	def column2x(c:Int):Int = c * params.tileWidth + colDelims.take(c+1).sum
	def row2y(r:Int):Int = r * params.tileHeight + rowDelims.take(r+1).sum
	def toMatrixPosition(p:Point) = new MatrixPosition(x2column(p.x), y2row(p.y))
	def rowRange(r:R):Range = {
		var ds = colLeaves.map(l => data.get(r, l.obj.asInstanceOf[C]))
		var filtered = ds.filter(filter).map(q => transform(toDouble(q)))
		return if (filtered.isEmpty)
					Range.EMPTY
				else
					new Range(filtered.min, StatUtils.mean(filtered.toArray), filtered.max)
	}
	def get(ri:Int, ci:Int):D = 
		data.get(
				rowLeaves(ri).obj.asInstanceOf[R],
				colLeaves(ci).obj.asInstanceOf[C])
	def x2column(x:Double):Int = {
		if (x < 0) 
			return -1
		else {
			for (c <- 0 until nColumns) {
				var x0 = column2x(c)
				if (x0 <= x && x < x0 + params.tileWidth) 
					return c
				if (x < x0)
					return -1
			}
			return -1
		}
	}
	def y2row(y:Double):Int = {
		if (y < 0) 
			return -1
		else {
			for (r <- 0 until nRows) {
				var y0 = row2y(r)
				if (y0 <= y && y < y0 + params.tileHeight) 
					return r
				if (y < y0)
					return -1
			}
			return -1
		}
	}
	
	
	
	
	/*============== paint functions =====================*/
	override def paintComponent(g:Graphics2D) = {
		super.paintComponent(g)
		
		var clipRect = g.getClipBounds
		
		/* update if dirty */
		if (paramsDirty)
			globalRange = calculateGlobalRange
		
		g.setColor(params.hmBGColor)
		
		var rect:Rectangle = new Rectangle(0, 0, params.tileWidth, params.tileHeight)
		var fm = g.getFontMetrics(g.getFont)
		
		
		/*
		// Heatmap
		var y = 0
		var x0 = clipRect.getX
		var xn = clipRect.getX + clipRect.getWidth
		var y0 = clipRect.getY
		var yn = clipRect.getY + clipRect.getHeight
		var ri = 0
		while (y < yn && ri < rowLeaves.length) {
			var x = 0
			var rd = rowDelims(ri)
			
			if (rd > 0 && y+rd > y0) {
				g.setColor(params.hmBGColor)
				g.fill(new Rectangle(x, y, pxWidth, rowDelims(ri)))
			}
			y += rd
			
			if (y + params.tileHeight > y0) {
				import HeatMapParams._
				var range = params.coloringScheme match {
					case Global() => globalRange
					case Row() => rowRange(rowLeaves(ri).obj.asInstanceOf[R])
					case Fixed(min, mid, max) => new Range(min, mid, max)
				}
				
				var ci = 0
				rect.y = y
				while (x < xn && ci < colLeaves.length) {
					// col delim
					var cd = colDelims(ci)
					if (cd > 0 && x+cd > x0) {
						rect.x = x
						rect.width = cd
						g.setColor(params.hmBGColor)
						g.fill(rect)
					}
					x += cd
					
					// data point
					if (x + params.tileWidth > x0) {
						rect.x = x
						rect.width = params.tileWidth
						var d = get(ri, ci)
						try {
							g.setColor(
								if (params.filter(d)) 	
									params.colorTransform.getColor(
										normalize(
											params.transform(
													params.toDouble(d)
											),
											range
										)
									)
								else params.special(d)
							)
						} catch {
							case e:Exception => {} //println("Color error r"+ri+":c"+ci)
						}
						g.fill(rect)
					}
					
					x += params.tileWidth
					ci += 1
				}
			}
			
			y += params.tileHeight
			ri += 1
		}
		*/
		
		var y = 0
		for (ri <- 0 until rowLeaves.length) {
			var x = 0
			import HeatMapParams._
			var range = params.coloringScheme match {
				case Global() => globalRange
				case Row() => rowRange(rowLeaves(ri).obj.asInstanceOf[R])
				case Fixed(min, mid, max) => new Range(min, mid, max)
			}
			//println("Row "+ri+", "+range)
			
			// row delim
			if (rowDelims(ri) > 0) {
				g.setColor(params.hmBGColor)
				g.fill(new Rectangle(x, y, pxWidth, rowDelims(ri)))
			}
			y += rowDelims(ri)
			for (ci <- 0 until colLeaves.length) {
				// col delim
				var dx = colDelims(ci)
				rect.x = x
				rect.y = y
				rect.width = dx
				g.setColor(params.hmBGColor)
				g.fill(rect)
				
				// data point
				x += dx
				rect.x = x
				rect.y = y
				rect.width = params.tileWidth
				var d = get(ri, ci)
				try {
					g.setColor(
					if (params.filter(d)) 	
						params.colorTransform.getColor(
							normalize(
								params.transform(params.toDouble(d)),
								range
							)
						)
					else
						params.special(d)
				)
				} catch {
					case e:Exception => {} //println("Color error r"+ri+":c"+ci)
				}
				
				g.fill(rect)
				x += params.tileWidth
			}
			y += params.tileHeight
		}
		
		// Interaction
		if (highlighted.isValid) {
			g.setColor(params.highlightColor)
			rect.x = column2x(highlighted.pos.column)
			rect.y = row2y(highlighted.pos.row)
			g.draw(rect)
		}
		
		if (selected.isValid) {
			g.setColor(params.selectionColor)
			g.setStroke(new java.awt.BasicStroke(3.0f))
			rect.x = column2x(selected.pos.column)
			rect.y = row2y(selected.pos.row)
			g.draw(rect)
			g.setStroke(new java.awt.BasicStroke(1.0f))
		}
		
		// Interaction
		if (highlighted.isValid) {
			g.setColor(params.highlightColor)
			rect.x = column2x(highlighted.pos.column)
			rect.y = row2y(highlighted.pos.row)
			g.draw(rect)
		}
		
		if (selected.isValid) {
			g.setColor(params.selectionColor)
			g.setStroke(new java.awt.BasicStroke(3.0f))
			rect.x = column2x(selected.pos.column)
			rect.y = row2y(selected.pos.row)
			g.draw(rect)
			g.setStroke(new java.awt.BasicStroke(1.0f))
		}
	}
	
	
	
	
	/*============== utility functions =====================*/
	private def normalize(d:Double, r:Range):Double = {
		import se.lth.immun.graphs.util.ColorTransform._
		if (d < r.mid)
			return relPos(math.max(d, r.min), r.min, r.mid) / 2
		else
			return relPos(math.min(d, r.max), r.mid, r.max) / 2 + 0.5
	}
						
	
	
	
	private def treeIterate(
					n:HMTree,
					leaf:(Any) => Unit,
					between:(Any, Int) => Unit = (s, i) => {}
		):Int = {
			n.children.length match {
				case 0 => { leaf(n.obj); return 0 }
				case 1 => return treeIterate(n.children(0), leaf, between)+1
				case _ => {
					var h = n.children.map(_.lastHeight).max
					treeIterate(n.children(0), leaf, between)
					for (c <- n.children.tail) { 
						between(n.obj, h)
						treeIterate(c, leaf, between)
					}
					return h + 1
				}
			}
		}
	private def getDelims(n:HMTree):Array[Int] = {
		var delims = Array(0).padTo(n.width, 0)
		var index = 0
		n.height
		treeIterate(n,
				a => index += 1,
				(a, h) => {
					delims(index) = h * params.sep
				})
		return delims
	}
}