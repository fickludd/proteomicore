package se.lth.immun.group

import se.lth.immun.collection.AbstractTree

object GroupTree extends AbstractTree {
	type N = Node
	
	class Node(
			var name:String, 
			var metaType:String = null,
			var value:Double = Double.NaN
	) extends AbstractNode {
		
		override def nodeName = name
		override def toString = treeString()
		
		
		/*def getLevels():Array[Seq[Node]] = {
			var arr = new Array[Seq[Node]](height)
			arr(0) = children
			for (l <- 1 until arr.length) {
				arr(l) = arr(l-1).flatMap(_.children)
			}
			return arr
		}
		
		
		
		def iterate(
					leaf:(String, Int) => Unit,
					between:(String, Int) => Unit = (s, i) => {},
					height:Int = 0
		):Unit = {
			children.length match {
				case 0 => leaf(name, height)
				case 1 => children(0).iterate(leaf, between, height+1)
				case _ => {
					children(0).iterate(leaf, between, height+1)
					for (child <- children.tail) { 
						between(name, height)
						child.iterate(leaf, between, height+1)
					}
				}
			}
		}
		
		
		
		def modify(
					leaf:(Node) => Unit,
					between:(Node) => Unit = g => {},
					height:Int = 0
		):Unit = {
			children.length match {
				case 0 => leaf(this)
				case 1 => children(0).modify(leaf, between, height+1)
				case _ => {
					children(0).modify(leaf, between, height+1)
					for (child <- children.tail) { 
						between(this)
						child.modify(leaf, between, height+1)
					}
				}
			}
		}
		
		
		
		def filter(
					keep:(Node) => Boolean,
					childrenFirst:Boolean = false
		):Unit = {
			if (childrenFirst) {
				children.foreach(_.filter(keep, childrenFirst))
				children = children.filter(c => keep(c))
			} else {
				children = children.filter(c => keep(c))
				children.foreach(_.filter(keep, childrenFirst))
			}
		}
		*/
	}
}

/*
class GroupTreeNode(
		var name:String,
		var children:Seq[GroupTreeNode] = Nil,
		var metaType:String = null
) {
	override def toString = treeString()
	def treeString(prefix:String = ""):String = 
		prefix + name + "\n" + (if (isParent) 
									children.map(_.treeString(prefix + " ")).reduceRight(_ + _)
								else "") 
	
	
	def isParent = children.length > 0
	def height:Int = 	if (isParent) 	children.map(_.height).max + 1
						else			0
	def width:Int = 	if (isParent)	children.map(_.width).sum
						else			1
	
						
	
	
}

*/
