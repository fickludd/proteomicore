package se.lth.immun.graphs

import se.lth.immun.collection.AbstractAnyTree

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Font
import java.awt.FontMetrics
import java.awt.image.BufferedImage

import scala.swing._

object BlockTreeGraph extends AbstractAnyTree {
	type N = Node
	
	var font = new Font("Arial", Font.PLAIN, 10)
	var fm = 
		(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)).createGraphics.getFontMetrics(font)
		
		
		
	object NodeState extends Enumeration {
		type State = Value
		val Normal, Highlight, Select = Value
	}
	
	
	
	class Node(
			override var obj:Any,
			childs:Seq[N] = Nil
	) extends AbstractAnyNode {
		
		for (c <- childs) addChild(c)
		
		import NodeState._
		def setState(state:State) = _nodeState = state
		def ifSetState(now:State, then:State):Boolean = 
			if (_nodeState == now) {
				_nodeState = then
				return true
			} else
				return false
		private var _nodeState:State = Normal
		private var _treeState:State = Normal
		def updateTreeState:State = {
			if (_nodeState != Normal)
				_treeState = _nodeState
			else {
				 var states = children map (_.updateTreeState)
				 if (states exists (_ == Select)) _treeState = Select
				 else if (states exists (_ == Highlight)) _treeState = Highlight
				 else _treeState = Normal
			}
			_treeState
		}
		
		override def nodeName = obj.toString
		override def toString = treeString()
		
		def paint(
				g:Graphics2D, 
				metrics:Metrics, 
				tw:Int, 
				x:Int, 
				y:Int, 
				reverse:Boolean = false
		):Unit = {
			var pb = pxBreadth(metrics)
			var pd = pxDepth(metrics)
			var psl = pxStrLength(metrics.obj2string(obj))
			var nodeWidth = psl + (if (isParent) (tw - pd) / 2 else tw - pd)
						
			var rect:Rectangle = 	
				if (reverse)	new Rectangle(x + tw - nodeWidth, y, nodeWidth, pb)
				else			new Rectangle(x, y, nodeWidth, pb)
			
			paintSelf(g, rect, psl, metrics)
			
			var py = y
			for (c <- children) {
				c.paint(
							g, 
							metrics, 
							tw - rect.width - metrics.levelSep, 
							if (reverse)
								x
							else
								x + rect.width + metrics.levelSep, 
							py,
							reverse
						)
				py += c.pxBreadth(metrics) + (height-1) * metrics.leafSep
			}
		}
		
		def paintSelf(
				g:Graphics2D,
				rect:Rectangle,
				pxStrLength:Int,
				metrics:Metrics
		) = {
			g.setColor(
						_treeState match {
							case Select => 	metrics.selectedColor
							case Highlight => metrics.highlightedColor
							case Normal => metrics.nodeColor
						}
					)
			g.fill(rect)
			g.setColor(metrics.textColor)
			g.setFont(font)
			g.drawString(
						metrics.obj2string(obj), 
						rect.x + (rect.width - pxStrLength) / 2, 
						rect.y + rect.height/2 - fm.getHeight / 2 + fm.getAscent
					)
		}
		
		def pxStrLength(str:String):Int = {
			var chars = str.toCharArray
			fm.charsWidth(chars, 0, chars.length)
		}
		
		def pxBreadth(metrics:Metrics):Int = {
			if (isParent) 	children.map(_.pxBreadth(metrics)).sum + 
							(children.length - 1) * metrics.leafSep * (height-1)
			else			metrics.leafBreadth
		}
		
		def pxDepth(metrics:Metrics):Int = {
			var psl = pxStrLength(metrics.obj2string(obj))
			if (isParent) 	(
								children.map(_.pxDepth(metrics)).max
								+ metrics.levelSep
								+ psl
							)
			else			metrics.levelSep + psl
		}
	}
	
	
	/*
	 * Normal tree orientation is root to the left.
	 * The different combinations of reverse and rotate twist 
	 * the tree according the table below.
	 * 
	 * ------------------------
	 * reverse  rotate  root
	 * ------------------------
	 *  false	 false	 left
	 *  true	 false	 right
	 *  false	 true	 bottom
	 *  true	 true	 top
	 * ------------------------
	 */
	class Tree(
			structure:AbstractAnyTree#AbstractAnyNode,
			var metrics:Metrics,
			var reverse:Boolean = false,
			var rotate:Boolean = false
	) extends Component {
		
		var root:Node = structure match {
					case n:Node => n
					case _ => convertAAN(structure)
				}
		
		
		override def toString = root.toString 
		var pxDepth = root.pxDepth(metrics)
		var pxBreadth = root.pxBreadth(metrics)
		var pxWidth = if (rotate) pxBreadth else pxDepth
		var pxHeight = if (rotate) pxDepth else pxBreadth
		
		minimumSize = new Dimension(pxWidth, pxHeight)
		maximumSize = new Dimension(pxWidth, pxHeight)
		preferredSize = new Dimension(pxWidth, pxHeight)
		
		private def convertAAN(aan:AbstractAnyTree#AbstractAnyNode):Node = 
			new Node(aan.obj, aan.children map (convertAAN(_)))
		
		def paint(g:Graphics2D, x:Int, y:Int) = {
			root.updateTreeState
			root.paint(
						g, 
						metrics, 
						pxDepth,
						x,
						y,
						reverse
					)
		}
		override def paintComponent(g:Graphics2D) = {
			super.paintComponent(g)
			if (rotate) {
				g.rotate(-90.0.toRadians)
				g.translate(-pxDepth, 0)
				paint(g, 0, 0)
				g.translate(pxDepth, 0)
				g.rotate(90.0.toRadians)
			} else 
				paint(g, 0, 0)
		}
	}
	
	
	
	class Metrics(
			var leafBreadth:Int,
			var leafSep:Int,
			var levelSep:Int,
			var x2string:PartialFunction[Any, String] = 
				{ case a:Any => a.toString}
	) {
		var textColor = Color.BLACK
		var nodeColor = Color.LIGHT_GRAY
		var highlightedColor = new Color(0xDDDDDD)
		var selectedColor = new Color(0xAAAAFF)
		
		def obj2string(obj:Any) = 
			if (x2string.isDefinedAt(obj))
				x2string(obj)
			else
				obj.toString
	}
}