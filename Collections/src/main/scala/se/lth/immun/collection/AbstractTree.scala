package se.lth.immun.collection

import scala.collection.mutable.Queue
import scala.collection.mutable.Buffer
import scala.collection.mutable.ArrayBuffer

trait AbstractTree {
	type N <: AbstractNode
	
	var root:Option[N] = None
	
	trait AbstractNode {self: N =>
		var children:Buffer[N] = new ArrayBuffer[N]()
		var parent:Option[N] = None
		
		def isParent = !children.isEmpty
		
		def addChild(child:N) = {
			def set = {
				children += child
				child.parent = Some(self)
			}
			child.parent match {
				case Some(p) => if (p != self) set
				case None => set
			}
		}
		def removeChild(child:N) = {
			def remove = {
				children -= child
				child.parent = None
			}
			child.parent match {
				case Some(p) => if (p == self) remove
				case None => {}
			}
		}
		
		private var _h:Int = 0
		def lastHeight = _h
		def height:Int = {
			_h = 	if (isParent) 	children.map(_.height).max + 1
					else			0
			_h
		}
		private var _w:Int = 0
		def lastWidth = _w
		def width:Int = {
			_w =	if (isParent)	children.map(_.width).sum
					else			1
			_w
		}
		def nodeName = "AbstractNode"
		def treeString(prefix:String = ""):String = 
			prefix + nodeName + "\n" + (if (isParent) 
									children.map(_.treeString(prefix + " ")).mkString
								else "")
		
		def depthFirst(f:N => Unit):Unit = {
			f(this)
			children foreach (_.depthFirst(f))
		}
		
		def breadthFirst(f:N => Unit):Unit = {
			var q = new Queue[N]()
			q += this
			while (!q.isEmpty) {
				var curr = q.dequeue
				curr.children foreach (q += _)
				f(curr)
			}
		}
		
		def build(f:N => Unit):Unit = {
			var q = new Queue[N]()
			q += this
			while (!q.isEmpty) {
				var curr = q.dequeue
				f(curr)
				curr.children foreach (q += _)
			}
		}
		
		def collect[R](pf:PartialFunction[N, R]):Seq[R] = 
			if (pf.isDefinedAt(this))
				List(pf(this))
			else
				children.flatMap(_.collect(pf))
		
		def leaves:Seq[N] = {
				var l:List[N] = Nil
				depthFirst(n => if (!n.isParent) l = l :+ n)
				return l
			}
	}
}
