package se.lth.immun.graphs.treeview

import java.awt.datatransfer.Transferable
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.UnsupportedFlavorException
import javax.swing.tree.TreePath

object TreePathList {
	val DATA_FLAVOR = new DataFlavor(
							classOf[TreePathList], 
							DataFlavor.javaJVMLocalObjectMimeType)
}

class TreePathList(
		var list:Seq[TreePath]
) extends Transferable {

	
	/** Transferable interface */
	import TreePathList._
	def getTransferDataFlavors() = Array(DATA_FLAVOR)
	
	def isDataFlavorSupported(df:DataFlavor) = 
			df == DATA_FLAVOR

	@throws(classOf[UnsupportedFlavorException])
	def getTransferData(df:DataFlavor):AnyRef = 
		if (df == DATA_FLAVOR) return list
		else throw new UnsupportedFlavorException(df)
}