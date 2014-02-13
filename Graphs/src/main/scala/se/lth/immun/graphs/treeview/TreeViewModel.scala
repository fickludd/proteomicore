package se.lth.immun.graphs.treeview

import java.awt.datatransfer.Transferable
import javax.swing.tree.{TreeModel, TreePath}
import javax.swing.event.{TreeModelListener, TreeModelEvent}
import se.lth.immun.collection.TTree
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Queue




class TreeViewModel(
		val tree:TTree[Transferable],
		val allowDuplicates:Boolean = false
) extends TreeModel {
	def this(objs:Seq[Transferable]) = 
		this(new TransferableTTree {
				root = Some(new Node(
								TransferableTTree.NOT_TRANSFERABLE_ROOT, 
								objs.map(c => new Node(c)) 
							))
			})
	
	
	type Node = TTree[Transferable]#TNode
	def cast(p:Any) = p.asInstanceOf[tree.N]
	
	
	var freezeReload = false
	private var listeners = new ArrayBuffer[TreeModelListener]()
	
	/** TreeModel interface */
	def addTreeModelListener(l:TreeModelListener) 		= listeners += l
	def removeTreeModelListener(l:TreeModelListener) 	= listeners -= l
	def getChild(parent:Any, index:Int):AnyRef = 
			cast(parent).children(index)
	def getChildCount(parent:Any) = 
			cast(parent).children.length
	def getIndexOfChild(parent:Any, child:Any) = 
			cast(parent).children.indexOf(cast(child))
	def getRoot = tree.root.get
	def isLeaf(node:Any) = false //!cast(node).isParent
	def valueForPathChanged(path:TreePath, newValue:Any) = {}
	/** TreeModel interface */
	
	
	
	
	
	
	def addNode(t:Transferable, parentPath:TreePath):Option[TreePath] = {
		if (!inTree(parentPath)) return None
		var child = tree.createNode(t)
		var parent = cast(parentPath.getLastPathComponent)
		parent.addChild(child)
		reload(parent)
		return Some(getPath(child))
	}
	
	
	
	def addPath(extension:TreePath, suggestedParent:TreePath = null):Boolean = {
		if (!inTree(suggestedParent)) return false
		var newLeaf = extension.getLastPathComponent()
		if (!allowDuplicates && findNode(_ == newLeaf).isDefined) return false
		
		var parentNode = cast(suggestedParent.getLastPathComponent())
		for (obj <- extension.getPath) {
			parentNode.children.find(_.obj == obj) match {
				case Some(node) => {
					parentNode = node
				}
				case None => {
					var child = tree.createNode(obj)
					parentNode.addChild(child)
					parentNode = child
				}
			}
		}
		reload(suggestedParent)
		return true
	}
	
	
	
	def addTree(tree:TTree[Transferable], parentPath:TreePath, includeRoot:Boolean):Option[TreePath] = {
		if (!inTree(parentPath)) return None
		if (!tree.root.isDefined) return None
		
		var q = new Queue[Tuple2[tree.N, this.tree.N]]()
		var child = this.tree.createNode(tree.root.get.obj)
		q += new Tuple2(tree.root.get, child)
		while (!q.isEmpty) {
			var curr = q.dequeue
			curr._1.children foreach (n => {
				var t = this.tree.createNode(n.obj)
				curr._2.addChild(t)
				q += new Tuple2(n, t)
			})
		}
		
		var parent = cast(parentPath.getLastPathComponent)
		if (includeRoot)
			parent.addChild(cast(child))
		else {
			for (c <- child.children)
				parent.addChild(cast(c))
		}
		reload(parent)
		return Some(getPath(child))
	}
	
	
	
	def clear():Boolean = {
		if (!tree.root.isDefined) return false
		var r = tree.root.get
		for (c <- r.children)
			c.parent = None
		r.children.clear
		return true
	}
	
	
	
	def findNode(f:Transferable => Boolean):Option[TreePath] = {
		if (!tree.root.isDefined) return None
		tree.root.get.breadthFirst(n => {
			if (f(n.obj))
				return Some(getPath(n))
		})
		return None
	}
	
	
	
	def getDeepest(relationOk:(Transferable, Transferable) => Boolean
				)(obj:Transferable, start:TreePath
	):TreePath = {
		if (start == null) return getPath(getRoot)
		var curr = start
		while (!relationOk(obj, cast(curr.getLastPathComponent).obj) && 
				curr.getPathCount > 1)
			curr = curr.getParentPath
		return curr
	}
	
	
	
	def getPath(n:Node):TreePath = new TreePath(getPathArray(n).toArray.asInstanceOf[Array[AnyRef]])
	
	
	
	def getPathArray(n:Node):Seq[Node] = {
		var curr:Option[Node] = Some(n)
		var l:Seq[Node] = Nil
		while (curr.isDefined) {
			l = l :+ curr.get
			curr = curr.get.parent
		}
		l.reverse
	}
	
	
	
	def getTransferable(path:TreePath):Transferable = {
		return cast(path.getLastPathComponent).obj
	}
	
	
	
	def getTree(path:TreePath):Option[TransferableTTree] = {
		if (path != null && !inTree(path)) return None
		var node = 	if (path == null) 	tree.root.get 
					else 				cast(path.getLastPathComponent)
		return Some(new TransferableTTree {
			root = Some({
				var q = new Queue[Tuple2[tree.N, N]]()
				var t0 = createNode(node.obj)
				q += new Tuple2(node, t0)
				while (!q.isEmpty) {
					var curr = q.dequeue
					curr._1.children foreach (n => {
						var t = createNode(n.obj)
						curr._2.addChild(t)
						q += new Tuple2(n, t)
					})
				}
				t0
			})
		})
	}
	
	
	
	def inTree(n:Node):Boolean = {
		var path = getPathArray(n)
		return tree.root.isDefined && path(0) == tree.root.get
	}
	
	
	
	def inTree(path:TreePath):Boolean = {
		var pa = path.getPath
		for (i <- 0 until (pa.length-1)) {
			var j = pa.length - 1 - i
			var n = cast(pa(j))
			if (!n.parent.isDefined || n.parent.get != pa(j-1))
				return false
		}
		var pa0 = pa(0)
		var trg = tree.root.get
		var ok = tree.root.isDefined && pa0 == trg 
		return ok
	}
	
	
	
	def moveNode(path:TreePath, to:TreePath):Boolean = {
		if (path == null || !inTree(path) || path.getPathCount <= 1) return false
		var node = cast(path.getLastPathComponent)
		var oldParent = node.parent.get
		var newParent = cast(to.getLastPathComponent)
		oldParent.removeChild(node)
		newParent.addChild(node)
		reload(oldParent)
		reload(newParent)
		return true
	}
	
	
	
	def printTree:Unit = println(tree.root.get.treeString())
	
	
	
	def removeNode(path:TreePath):Boolean = {
		if (!inTree(path)) return false
		var node = cast(path.getLastPathComponent)
		var parent = node.parent.get
		parent.removeChild(node)
		reload(parent)
		return true
	}
	
	
	
	def reload:Unit = reload(tree.root.get)
	
	
	
	def reload(node:Node):Unit = 
		if(node != null && !freezeReload)
			fireTreeStructureChanged(this, getPathArray(cast(node)))
	
	
	
	def reload(path:TreePath):Unit = 
		if(path != null)
			fireTreeStructureChanged(this, path.getPath())
	
	
	
	private def fireTreeStructureChanged(source:Any, path:Seq[AnyRef]) = {
        var e = new TreeModelEvent(source, path.toArray, null, null)
        for (l <- listeners.reverse) l.treeStructureChanged(e)
    }
}