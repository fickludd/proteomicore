package se.lth.immun.graphs.treeview

import java.awt.datatransfer.Transferable
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.UnsupportedFlavorException
import se.lth.immun.collection.TTree




object TransferableTTree {
	val DATA_FLAVOR = new DataFlavor(
							classOf[TransferableTTree], 
							DataFlavor.javaJVMLocalObjectMimeType)
	
	object NOT_TRANSFERABLE_ROOT extends Transferable {
		override def toString = "root"
		
		/** Transferable interface */
		def getTransferDataFlavors() = Array()
		def isDataFlavorSupported(df:DataFlavor) = false
	
		@throws(classOf[UnsupportedFlavorException])
		def getTransferData(df:DataFlavor):AnyRef = 
			throw new UnsupportedFlavorException(df)

	}
}




class TransferableTTree extends TTree[Transferable] with Transferable { 
	type N = Node
	
	var includeRoot = true
	def createNode(t:Any):N = new Node(t.asInstanceOf[Transferable])
	override def toString:String = "TransferableTTree("+
										(if (root.isDefined)
											root.get.obj.toString
											else ""
										)+")"
	
	/** Transferable interface */
	import TransferableTTree._
	def getTransferDataFlavors() = Array(DATA_FLAVOR) ++ 
			(	if (root.isDefined) 	root.get.obj.getTransferDataFlavors()
				else 					Nil
			)
	def isDataFlavorSupported(df:DataFlavor) = 
			df == DATA_FLAVOR || 
			(root.isDefined && root.get.obj.isDataFlavorSupported(df))

	@throws(classOf[UnsupportedFlavorException])
	def getTransferData(df:DataFlavor):AnyRef = 
		if (df == DATA_FLAVOR) return this
		else if (root.isDefined) return root.get.obj.getTransferData(df)
		else throw new UnsupportedFlavorException(df)
	
	
	
	class Node(
			_o:Transferable, 
			_childs:Seq[N] = Nil
	) extends TNode(_o, _childs) {self:N => 
		override def toString = "TransferableTTree.Node("+obj+")"+this.hashCode}
}