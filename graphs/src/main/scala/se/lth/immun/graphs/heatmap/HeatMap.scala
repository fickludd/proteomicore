package se.lth.immun.graphs.heatmap

import swing._
import swing.event._
import java.awt.Color
import se.lth.immun.collection.AbstractAnyTree
import se.lth.immun.collection.DataMatrix
import se.lth.immun.graphs.BlockTreeGraph

class HeatMap[R, C, D](
		data:DataMatrix[R, C, D],
		var params:HeatMapParams[D],
		x2string:PartialFunction[Any, String] = { case a:Any => a.toString},
		var legend:Component = null
) extends BoxPanel(Orientation.Vertical) {
	
	
	
	def setData(data:DataMatrix[R, C, D]) = {
		heatMap.setData(data)
		rowTree = new BlockTreeGraph.Tree(
							data.rowGrouping,
							new BlockTreeGraph.Metrics(
									params.tileHeight,
									params.sep,
									params.sep,
									x2string))
		colTree = new BlockTreeGraph.Tree(
							data.columnGrouping,
							new BlockTreeGraph.Metrics(
									params.tileWidth,
									params.sep,
									params.sep,
									x2string),
							true, true)
		pxWidth 		= rowTree.pxWidth + heatMap.pxWidth
		pxHeight 		= colTree.pxHeight + heatMap.pxHeight
		preferredSize 	= new Dimension(pxWidth, pxHeight) 
		maximumSize 	= new Dimension(pxWidth, pxHeight)
		rowLeaves 		= rowTree.root.leaves
		colLeaves 		= colTree.root.leaves
		legendScrollPane.minimumSize 	= new Dimension(rowTree.pxWidth, colTree.pxHeight)
		legendScrollPane.maximumSize 	= new Dimension(rowTree.pxWidth, colTree.pxHeight)
		legendScrollPane.preferredSize 	= new Dimension(rowTree.pxWidth, colTree.pxHeight)
		constructLayout
	}
	
	
	
	/* display objects */
	var heatMap = new BareHeatMap(data, params)
	var rowTree = new BlockTreeGraph.Tree(
							data.rowGrouping,
							new BlockTreeGraph.Metrics(
									params.tileHeight,
									params.sep,
									params.sep,
									x2string))
	var colTree = new BlockTreeGraph.Tree(
							data.columnGrouping,
							new BlockTreeGraph.Metrics(
									params.tileWidth,
									params.sep,
									params.sep,
									x2string),
							true, true)
	
	
	
	/* size */
	var pxWidth 	= rowTree.pxWidth + heatMap.pxWidth
	var pxHeight 	= colTree.pxHeight + heatMap.pxHeight
	preferredSize 	= new Dimension(pxWidth, pxHeight) 
	maximumSize 	= new Dimension(pxWidth, pxHeight)
	
	
	
	/* legend */
	var legendScrollPane = 	if (legend != null) 	new ScrollPane(legend)
							else					new ScrollPane
	legendScrollPane.minimumSize 	= new Dimension(rowTree.pxWidth, colTree.pxHeight)
	legendScrollPane.maximumSize 	= new Dimension(rowTree.pxWidth, colTree.pxHeight)
	legendScrollPane.preferredSize 	= new Dimension(rowTree.pxWidth, colTree.pxHeight)
	
	
	
	/* layout */
	constructLayout
	def constructLayout = {
		contents.clear
		contents += new BoxPanel(Orientation.Horizontal) {
			contents += legendScrollPane
			contents += colTree
		}
		contents += new BoxPanel(Orientation.Horizontal) {
			contents += rowTree
			contents += heatMap
		}
	}
	
	
	
	
	
	/* interaction */
	/* header leaves */
	var rowLeaves = rowTree.root.leaves
	var colLeaves = colTree.root.leaves
	/* input */
	def selectedColumn 	= heatMap.selectedColumn
	def selectedRow 	= heatMap.selectedRow
	def selectLeft 		= heatMap.selectLeft
	def selectRight 	= heatMap.selectRight
	def selectUp 		= heatMap.selectUp
	def selectDown 		= heatMap.selectDown
	/* response */
	var selectionReactor = new Reactor { 
		import BlockTreeGraph.NodeState._
		
		listenTo(heatMap.selected)
		reactions += {
			case sc:SelectionChanged => {
				var s = heatMap.selected
				if (s.old.row != s.pos.row) {
					if (s.wasValid) rowLeaves(s.old.row).setState(Normal)
					if (s.isValid) rowLeaves(s.pos.row).setState(Select)
					if (s.wasValid || s.isValid) rowTree.repaint
				}
				if (s.old.column != s.pos.column) {
					if (s.wasValid) colLeaves(s.old.column).setState(Normal)
					if (s.isValid) colLeaves(s.pos.column).setState(Select)
					if (s.wasValid || s.isValid) colTree.repaint
				}
				publish(new SelectionChanged(heatMap))
			}
		}
	}
	var highlightReactor = new Reactor {
		import BlockTreeGraph.NodeState._
			
		listenTo(heatMap.highlighted)
		reactions += {
			case sc:SelectionChanged => {
				var h = heatMap.highlighted
				if (h.old.row != h.pos.row) {
					if (h.validRow(h.old.row))
						if (rowLeaves(h.old.row).ifSetState(Highlight, Normal))
							rowTree.repaint
					if (h.validRow(h.pos.row))
						if (rowLeaves(h.pos.row).ifSetState(Normal, Highlight))
							rowTree.repaint
				}
				if (h.old.column != h.pos.column) {
					if (h.validColumn(h.old.column))
						if (colLeaves(h.old.column).ifSetState(Highlight, Normal))
							colTree.repaint
					if (h.validColumn(h.pos.column))
						if (colLeaves(h.pos.column).ifSetState(Normal, Highlight))
							colTree.repaint
				}
			}
		}
	}
}