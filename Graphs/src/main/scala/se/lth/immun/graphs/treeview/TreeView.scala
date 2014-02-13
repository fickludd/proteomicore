package se.lth.immun.graphs.treeview

import javax.swing.tree.TreePath
import swing._
import swing.event._




class TreeView(
		var model:TreeViewModel,
		var params:TreeViewParams
) extends Component with Publisher {
	override lazy val peer:DnDJTree = 
		new DnDJTree(model, params) with SuperMixin
	
	
	
	object selection {
		def paths:Seq[TreePath] = peer.getSelectionPaths() match { 
			case null => Nil
			case a:Array[TreePath] => a 
		}
		def indices:Seq[Int] = peer.getSelectionRows() match {
			case null => Nil
			case a:Array[Int] => a 
		}
	}
	
	
	def getDeepest = model.getDeepest(params.relationOk) _
}


