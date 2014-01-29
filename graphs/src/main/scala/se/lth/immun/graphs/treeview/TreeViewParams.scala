package se.lth.immun.graphs.treeview

import java.awt.datatransfer.Transferable
import java.awt.datatransfer.DataFlavor

class TreeViewParams(
		var renderFunction:Any => String,
		var editable:Any => Boolean,
		
		// potential child, parent and tells if the relation is ok.
		var relationOk:(Any, Any) => Boolean,
		var updateObj:(Any, String) => Unit,
		var supportedDataFlavors:Seq[DataFlavor]
) {

}