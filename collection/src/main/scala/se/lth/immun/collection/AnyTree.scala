package se.lth.immun.collection

trait AbstractAnyTree extends AbstractTree {
	type N <: AbstractAnyNode
	
	trait AbstractAnyNode extends AbstractNode with AnyHolder {self:N => }
}

object AnyTree extends AbstractAnyTree {
	type N = Node
	
	class Node(
			override var obj:Any, 
			childs:Seq[N] = Nil
	) extends AbstractAnyNode {
		
		for (c <- childs) addChild(c)
		
		override def nodeName = obj.toString
		override def toString = treeString()
	}
}
