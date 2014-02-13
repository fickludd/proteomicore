package se.lth.immun.graphs.heatmap

import scala.swing.Component
import scala.swing.Publisher
import scala.swing.event.SelectionChanged

object MatrixPosition {
	val INVALID = new MatrixPosition(-1, -1)
}

class MatrixPosition(
		var column:Int,
		var row:Int
) extends Publisher {
	
	def up = new MatrixPosition(column, row-1)
	def right = new MatrixPosition(column+1, row)
	def down = new MatrixPosition(column, row+1)
	def left = new MatrixPosition(column-1, row)
	
	override def equals(arg0:Any) = arg0 match {
		case dp:MatrixPosition => dp.column == column && dp.row == row
		case _ => super.equals(arg0)
	}
	
	def isValid(nColumns:Int = Int.MaxValue, nRows:Int = Int.MaxValue) = 
		column >= 0 && column < nColumns && row >= 0 && row < nRows
}

class MatrixSelection(
		var source:Component,
		var nColumns:Int = Int.MaxValue, 
		var nRows:Int = Int.MaxValue
) extends Publisher {
	var old = MatrixPosition.INVALID
	var pos = MatrixPosition.INVALID
	
	def publish():Unit = publish(new SelectionChanged(source))
	def isValid = pos.isValid(nColumns, nRows)
	def wasValid = old.isValid(nColumns, nRows)
	def validRow(ir:Int) = 0 <= ir && ir < nRows
	def validColumn(ic:Int) = 0 <= ic && ic < nColumns
	def changed = pos != old
	def select(pos:MatrixPosition) = {
		old = this.pos
		this.pos = pos
		if (changed) publish
	}
	def reset(
		nCol:Int = Int.MaxValue, 
		nRow:Int = Int.MaxValue
	) = {
		nColumns = nCol
		nRows = nRow
		old = MatrixPosition.INVALID
		pos = MatrixPosition.INVALID
	}
}