package se.lth.immun.collection

import org.apache.commons.math3.stat.StatUtils


class DataMatrix[R, C, D](
		var data:Array[Array[D]],
		var rows:Seq[R],
		var cols:Seq[C],
		var rowGrouping:AbstractAnyTree#AbstractAnyNode = null,
		var columnGrouping:AbstractAnyTree#AbstractAnyNode = null
) {
	var colComparator:C => C => Boolean = (c1) => (c2) => c1 == c2
	var rowComparator:R => R => Boolean = (r1) => (r2) => r1 == r2 
  
  
	/* Data statistics */
	private def foldData(f:Array[Double] => Double)(
			toDouble:D => Double,
			filter:(D => Boolean),
			transform:(Double => Double) = d => d
	):Double = {
		var a = data.flatMap(
				_.filter(filter)
				 .map(q => transform(toDouble(q)))
				)
		if (a.isEmpty) Double.NaN else f(a)
	}

	def max = foldData(_.max) _
	def min = foldData(_.min) _
	def mean = foldData(StatUtils.mean(_)) _
	def median = foldData(StatUtils.percentile(_, 50.0)) _
	
	/* Defaul groups */
	if (columnGrouping == null)
		columnGrouping = new AnyTree.Node("root", cols.map(c => new AnyTree.Node(c)))
	
	if (rowGrouping == null)
		rowGrouping = new AnyTree.Node("root", rows.map(r => new AnyTree.Node(r)))
		
	if (!data.forall(_.length == cols.length))
		throw new IllegalArgumentException("Rows not all same length as column headers!")
	
	if (data.length != rows.length)
		throw new IllegalArgumentException("Number of rows not equal to the number of row headers!")
	
	
	
	
	def get(row:R, column:C):D = getColumn(column, getRow(row))
	def iGet(iRow:Int, iColumn:Int):D = data(iRow)(iColumn)
	
	
	
	
	def getRow(row:R):Array[D] = {
		var ri = rows.indexWhere(rowComparator(row))
		
		if (ri == -1)
			throw new IllegalArgumentException("'"+row+"' is not among row names.")
		
		return data(ri)
	}
	
	
	
	def getColumn(column:C, row:Array[D]):D = {
		var ci = cols.indexWhere(colComparator(column))
		
		if (ci == -1)
			throw new IllegalArgumentException("'"+column+"' is not among column names.")
		if (row.length != cols.length)
			throw new IllegalArgumentException("Row length not equal to column header length")
		
		return row(ci)
	}
	
	
	
	def getColumn(column:C):Seq[D] = {
		var ci = cols.indexWhere(colComparator(column))
		
		if (ci == -1)
			throw new IllegalArgumentException("'"+column+"' is not among column names.")
		
		return data.map(row => row(ci))
	}
	
	
	
	def getColumnHeader(column:C):Option[C] = 
		cols.find(colComparator(column))
	
	
	
	def getRowHeader(row:R):Option[R] = 
		rows.find(rowComparator(row))
		
	
	
	/*
	def use(
			row:String, 
			column:String, 
			action:Double => Unit, 
			special:Double => Unit = d => {}
	):Unit = {
		var d = get(row, column)
		if (filter(d)) 	action(transform(d))
		else			special(d)
	}
	
	
	
	def apply[T](
			row:String, 
			column:String, 
			normal:Double => T, 
			special:Double => T
	):T = {
		var d = get(row, column)
		if (filter(d)) 	return normal(transform(d))
		else			return special(d)
	}
	*/
}
