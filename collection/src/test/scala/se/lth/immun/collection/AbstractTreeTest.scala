package se.lth.immun.collection

import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before

class AbstractTreeTest extends AssertionsForJUnit {
	
	object Tree extends AbstractTree {
		type N = Node
		
		class Node(var name:String) extends AbstractNode {
			override def nodeName = name
		}
	}
	
	@Test 
	def createRoot() = {
		var t = new Tree.Node("root")
		assertFalse(t.isParent)
		assertEquals(0, t.height)
		assertEquals(1, t.width)
		assertEquals("root\n", t.treeString())
	}
	
	@Test
	def addChild() = {
		var t = new Tree.Node("root")
		t.addChild(new Tree.Node("child"))
		assertTrue(t.isParent)
		assertEquals(1, t.height)
		assertEquals(1, t.width)
		assertEquals("root\n child\n", t.treeString())
	}
	
	@Test
	def smallTree() = {
		var t = new Tree.Node("root")
		t.addChild(new Tree.Node("child1"))
		
		var b = new Tree.Node("branch")
		b.addChild(new Tree.Node("child2"))
		b.addChild(new Tree.Node("child3"))
		t.addChild(b)
		
		assertTrue(t.isParent)
		assertEquals(2, t.height)
		assertEquals(3, t.width)
		assertEquals("root\n child1\n branch\n  child2\n  child3\n", t.treeString())
		
		assertTrue(b.isParent)
		assertEquals(1, b.height)
		assertEquals(2, b.width)
	}
	
	@Test
	def depthFirst() = {
		var t = new Tree.Node("root")
		t.addChild(new Tree.Node("child1"))
		
		var b = new Tree.Node("branch")
		b.addChild(new Tree.Node("child2"))
		b.addChild(new Tree.Node("child3"))
		t.addChild(b)
		
		var sb = new StringBuilder()
		new Tree.Node("loose").depthFirst (sb ++= _.nodeName + " ")
		assertEquals("loose ", sb.toString)
		
		sb = new StringBuilder()
		b.depthFirst (sb ++= _.nodeName + " ")
		assertEquals("branch child2 child3 ", sb.toString)
		
		sb = new StringBuilder()
		t.depthFirst (sb ++= _.nodeName + " ")
		assertEquals("root child1 branch child2 child3 ", sb.toString)
	}
	
	@Test
	def breadthFirst() = {
		var t = new Tree.Node("root")
		
		var b1 = new Tree.Node("branch1")
		b1.addChild(new Tree.Node("child11"))
		b1.addChild(new Tree.Node("child12"))
		t.addChild(b1)
		
		var b2 = new Tree.Node("branch2")
		b2.addChild(new Tree.Node("child21"))
		b2.addChild(new Tree.Node("child22"))
		t.addChild(b2)
		
		var sb = new StringBuilder()
		new Tree.Node("loose").breadthFirst (sb ++= _.nodeName + " ")
		assertEquals("loose ", sb.toString)
		
		sb = new StringBuilder()
		b1.breadthFirst (sb ++= _.nodeName + " ")
		assertEquals("branch1 child11 child12 ", sb.toString)
		
		sb = new StringBuilder()
		t.breadthFirst (sb ++= _.nodeName + " ")
		assertEquals("root branch1 branch2 child11 child12 child21 child22 ", sb.toString)
	}
}
