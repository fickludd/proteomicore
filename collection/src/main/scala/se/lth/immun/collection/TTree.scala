package se.lth.immun.collection

trait TTree[+T] extends AbstractTree {
	type N <: TNode
	
	def createNode(t:Any):N
	
	override def toString = root match {
		case None => "Empty"
		case Some(r) => r.treeString()
	}
	
	class TNode (
			val t:T, 
			childs:Seq[N] = Nil
	) extends AbstractNode with Holder[T, N] {self:N => 
		
		for (c <- childs) addChild(c)
		
		override def nodeName = obj.toString
		override def toString = nodeName
		def obj = t
	}
}

/*
object TTree[T] extends AbstractTTree[T] {
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
*/
