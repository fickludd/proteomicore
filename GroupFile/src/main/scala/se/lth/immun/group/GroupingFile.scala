package se.lth.immun.group

import java.io.StringReader
import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlUtil
import se.lth.immun.xml.XmlWriter

object GroupingFile {
	val MAIN_NODE 	= "groupings"
    val GROUP 		= "group"
    val GROUP_TYPE 	= "type"
    val GROUP_VALUE = "value"
    	
    def main(args:Array[String]) = {
		val valid = """<?xml version="1.0"?>
<groupings>
	<group name="proteins" count="4">
		<group name="Ribosomes" count="10" value="2.0">
			<group name="RL4_STRP1" />
			<group name="RL29_STRA3" />
		</group>
	</group>
</groupings>"""
			
		val notEnded = """<?xml version="1.0"?>
<groupings>
	<group name="proteins" count="4">
		<group name="Ribosomes" count="10" value="2.0">
			<group name="RL4_STRP1" >
			<group name="RL29_STRA3" />
		</group>
	</group>
</groupings>"""
		val gf1 = new GroupingFile(new XmlReader(new StringReader(valid)))
		val gf2 = new GroupingFile(new XmlReader(new StringReader(notEnded)))
	}
}

class GroupingFile(r:XmlReader = null) {
	import GroupingFile._
	import XmlUtil._
	
	var root:GroupTree.Node = new GroupTree.Node("root")
	
	if (r != null) {
		r.until(MAIN_NODE)
		r.ensure(GROUP)
		
		root = parseGroupTree(r)
		
		r.close
	}
	
	
	
	def parseGroupTree(r:XmlReader):GroupTree.Node = {

		var node = new GroupTree.Node(r.readAttribute(NAME))
		if (r.hasAttribute(GROUP_TYPE))
			node.metaType = r.readAttribute(GROUP_TYPE)
		if (r.hasAttribute(GROUP_VALUE))
			node.value = r.readAttributeDouble(GROUP_VALUE)
		
		var e = r.top
		r.next
		while (r.in(e) && !r.atEOF)
			if (r.was(GROUP))
				node.addChild(parseGroupTree(r))
			else r.next
		
		def EOFok =
			r.queue.length == r.stack.filter(x => !x.name.startsWith("?")).length
		if (r.atEOF && !EOFok)
			throw new Exception("Invalid XML, element '%s' never ended!".format(e.elementStr))
			
		return node
	}
	
	
	
	def write(w:XmlWriter) = {
		w.startDocument
		w.startElement(MAIN_NODE)
		
		writeGroupTree(w, root)

        w.endElement
        w.endDocument
	}
	
	

    def writeGroupTree(w:XmlWriter, node:GroupTree.Node):Unit = {
		
		w.startElement(GROUP)
		w.writeAttribute(NAME, node.name)
		if (node.metaType != null)
			w.writeAttribute(GROUP_TYPE, node.metaType)
		if (!java.lang.Double.isNaN(node.value))
			w.writeAttribute(GROUP_VALUE, node.value)
		
		if (node.isParent) 	{
	        for (child <- node.children)
	            writeGroupTree(w, child)
	        w.endElement
		}
		w.endElement
	}
}
