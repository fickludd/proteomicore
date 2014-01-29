package se.lth.immun.graphs.treeview

import java.awt.Cursor
import java.awt.Point
import java.awt.dnd._
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.event.InputEvent
import java.io.IOException
import java.util.EventObject
import javax.swing.{JComponent, JTree, DropMode, SwingUtilities, JOptionPane, DefaultCellEditor, JTextField}
import javax.swing.tree.{TreeModel, TreePath, DefaultTreeCellRenderer,
							DefaultTreeCellEditor}
import javax.swing.event.{TreeModelListener, TreeSelectionListener, CellEditorListener,
							TreeSelectionEvent, TreeModelEvent, ChangeEvent}
import se.lth.immun.collection.TTree





object DnDJTree {
	/** ======================== RENDERER ============================ */
	class Renderer(
		var params:TreeViewParams
	) extends DefaultTreeCellRenderer {
		
		override def getTreeCellRendererComponent(
                        tree:JTree,
                        value:Any,
                        sel:Boolean,
                        expanded:Boolean,
                        leaf:Boolean,
                        row:Int,
                        hasFocus:Boolean):java.awt.Component = {

	        super.getTreeCellRendererComponent(
	                        tree, value, sel,
	                        expanded, leaf, row,
	                        hasFocus)
	        
	        setText(params.renderFunction(
	        		tree.asInstanceOf[DnDJTree].model.cast(value).obj
	        	))
	
	        return this
	    }
	}
	
	
	
	
	
	
	/** ======================== EDITOR ============================ */
	class Editor(
			var dndTree:DnDJTree, 
			var dndRenderer:Renderer,
			var params:TreeViewParams
	) extends DefaultTreeCellEditor(dndTree, dndRenderer) {
		
		var dce 	= realEditor.asInstanceOf[DefaultCellEditor]
		var tf 		= dce.getComponent.asInstanceOf[JTextField]
		dce.setClickCountToStart(2)
		
		
		override def isCellEditable(e:EventObject):Boolean = {
			var returnValue = super.isCellEditable(e)
			if (returnValue) {
		    	var node = dndTree.getLastSelectedPathComponent
		    	if (node != null) {
		    		var obj = dndTree.model.cast(node).obj
		    		return params.editable(obj)
		    	}
		    }
			return returnValue
		}
		
		override def getTreeCellEditorComponent(
                        tree:JTree,
                        value:Any,
                        sel:Boolean,
                        expanded:Boolean,
                        leaf:Boolean,
                        row:Int
        ):java.awt.Component = {

	        var ret = super.getTreeCellEditorComponent(
	                        tree, value, sel,
	                        expanded, leaf, row)
	        
	        tf.setText(params.renderFunction(dndTree.model.cast(value).obj))
	
	        return ret
	    }
		
		def text = tf.getText
	}
	
	
	
	
	
	class TransferHandler(
			tree:DnDJTree
	) extends javax.swing.TransferHandler {
		
		import javax.swing.TransferHandler._
		var dropSourcePath:TreePath = null
		
		
		override def getSourceActions(c:JComponent):Int = COPY_OR_MOVE
		override def createTransferable(c:JComponent):Transferable = {
			dropSourcePath = tree.selectedPath
			return tree.model.getTree(dropSourcePath).get
		}
		override def exportDone(c:JComponent, t:Transferable, action:Int) = {
		    if (action == MOVE && dropSourcePath != null) {
		        tree.model.removeNode(dropSourcePath)
		        dropSourcePath = null
		    }
		}
		
		override def canImport(supp:TransferSupport):Boolean = handleImport(false)(supp)
		override def importData(supp:TransferSupport):Boolean = handleImport(true)(supp)
		def handleImport(doImport:Boolean)(supp:TransferSupport):Boolean = {
			var dropTargetPath 	= getLoc(supp).getPath
		    var action 			= supp.getDropAction
			var data:AnyRef 	= null
			var tr				= supp.getTransferable
			
			def debug(		str:String = "") = print(	str)
			def debugln(	str:String = "") = println(	str)
			
			
			if (supp.isDataFlavorSupported(TransferableTTree.DATA_FLAVOR)) { // always if within tree
		    	var ttree 	= tr.getTransferData(TransferableTTree.DATA_FLAVOR)
		    							.asInstanceOf[TransferableTTree]
		    	
		    	data 		= ttree.root.get.obj
		    	
		    	if (doImport) {
		    		debugln()
		    		debugln("item: "+data)
		    		debugln("is Tree")
		    		debugln("destination: "+dropTargetPath)
		    		debugln("from: "+dropSourcePath)
		    	} else debug(" t")
				
				val msg 	= testDropTarget(dropTargetPath, data, dropSourcePath)
				if (msg != null) 	{ if (!doImport) debug(msg(0)+""); return false }
			    if (!doImport) 		{ debug("i"); return true }
			    
				//println("insert '"+ttree+"' at '"+dropTargetPath+"'")
			    if (action == MOVE && dropSourcePath != null) {
			    	debugln("move tree success: "+tree.model.moveNode(dropSourcePath, dropTargetPath))
			    	dropSourcePath = null
			    } else 
				    debugln("add tree success: "+tree.model.addTree(ttree, dropTargetPath, ttree.includeRoot))
			
			} else if (supp.isDataFlavorSupported(TreePathList.DATA_FLAVOR)) {
				
				var pathList = tr.getTransferData(TreePathList.DATA_FLAVOR).asInstanceOf[Seq[TreePath]]
				
				
				/*
				tree.params.supportedDataFlavors.find(df =>
						supp.isDataFlavorSupported(df)
					) match {
						case Some(df) => data = tr.getTransferData(df)
						case None => { /*print("x");*/ return false }
		    	}
		    	*/
				data = pathList.head.getPathComponent(0)
		    	if (doImport) {
		    		debugln()
		    		debugln("item: "+data)
		    		debugln("not Tree")
		    		debugln("destination: "+dropTargetPath)
		    		debugln("from: "+dropSourcePath)
		    	} else debug(" p")
		    	
				val msg 	= testDropTarget(
								dropTargetPath, 
								data, 
								dropSourcePath
							  )
				if (msg != null) 	{ if (!doImport) debugln(msg); return false }
			    if (!doImport) 		{ debug("i"); return true }
			    
			    debugln("insert '"+data+"' at '"+dropTargetPath+"'")
			    for (path <- pathList)
					debugln("adding '"+path.getPath().mkString(",")+"' worked: "+
							tree.model.addPath(path, dropTargetPath))
				
					/*tree.model.addNode(
							data.asInstanceOf[Transferable], 
							dropTargetPath)
							*/
			}
			return true
			
			
			/*
			var addTree 	= supp.isDataFlavorSupported(TransferableTTree.DATA_FLAVOR)
			var tr 			= supp.getTransferable
		    var data:AnyRef = null
	    	if (!addTree)
		    	tree.params.supportedDataFlavors.find(df =>
						supp.isDataFlavorSupported(df)
					) match {
						case Some(df) => data = tr.getTransferData(df)
						case None => { print("x"); return false }
		    	}
		
			var loc 	= getLoc(supp)
		    var ttree 	= if (addTree) tr.getTransferData(TransferableTTree.DATA_FLAVOR)
		    							.asInstanceOf[TransferableTTree]
		    				else null
			if (addTree) data = ttree.root.get.obj
			val msg 	= testDropTarget(loc.getPath, data, dropSourcePath)
			if (msg != null) 	{ print(msg(0))	; return false 	}
		    if (!doImport) 		{ println("i")	; return true 		}
		    
		    var action = supp.getDropAction
			println("insert '"+data+"' at '"+loc+"'")
		    if (action == MOVE && dropSourcePath != null)
		    	tree.model.moveNode(tree.selectedPath, loc.getPath)
		    else 
			    if (addTree) tree.model.addTree(ttree, loc.getPath)
				else tree.model.addNode(
						data.asInstanceOf[Transferable], 
						loc.getPath)
			return true
			*/
		}
	
		
		
		
		
		///////////////////////// helpers ////////////////////////////////////
		
		private def getLoc(supp:TransferSupport) = 
			supp.getDropLocation.asInstanceOf[JTree.DropLocation]
		
		
		/**
		 * Gets TransferableTTree if supported, otherwise first supported 
		 * data flavor in params.supportedDataFlavors or null 
		 */
		private def getData(tr:Transferable):AnyRef = {
			if (tr.isDataFlavorSupported(TransferableTTree.DATA_FLAVOR))
				tr.getTransferData(TransferableTTree.DATA_FLAVOR)
			else 
				tree.params.supportedDataFlavors.find(df =>
						tr.isDataFlavorSupported(df)
					) match {
						case Some(df) => tr.getTransferData(df)
						case None => null
					}
		}
		
	
		/** Convenience method to test whether drop location is valid
		@param destination 	The destination path 
		@param dragged 		The object to be dropped
		@param dropSource 	The path for the node to be dropped
		@return null if no problems, otherwise an explanation
		 */
		private def testDropTarget(
				destination:TreePath, 
				dragged:AnyRef,
				dropSource:TreePath
		):String = {
			/*
			println("testDropTarget: dest="+destination.getPath.mkString(",")+
					" dragged="+dragged+
					" dropSource="+(if (dropSource == null) "null" else dropSource.getPath.mkString(",")))
			*/
			if (destination == null) 
				return "1: Invalid drop location."
	
			var dropTarget = tree.model.getTransferable(destination)
			if (!tree.params.relationOk(dragged, dropTarget)) {
				//println("dragged: "+dragged+" dropTarget: "+dropTarget)
				return "2: A node '"+dropTarget+"'does not allow a child '"+dragged+"'";
			}
					
			if (destination == dropSource)
				return "3: The destination cannot be same as source"
	
			if (dropSource != null) {
				if (dropSource.isDescendant(destination)) 
					return "4: The destination node cannot be a descendant of the drop source."
	
				if (dropSource.getParentPath.equals(destination)) 
					return "5: The destination node cannot be the parent."
			}
			
			return null
		}
	}
}






class DnDJTree(
		val model:TreeViewModel,
		var params:TreeViewParams
) extends JTree(model)
						with TreeSelectionListener 
						with TreeModelListener 
						with CellEditorListener 
						with DragGestureListener
						with DropTargetListener
						with DragSourceListener {

	
	
	var selectedPath		:TreePath 	= null
	var dropTargetPath		:TreePath	= null
	/*var dt 								= new DropTarget(this, this)
	private var dragSource 			= new DragSource()
	var dgr = dragSource.createDefaultDragGestureRecognizer(
	    		this,                             //DragSource
	    		DnDConstants.ACTION_COPY_OR_MOVE, //specifies valid actions
	    		this                              //DragGestureListener
	    	)
	    	*/
	var renderer = new DnDJTree.Renderer(params)
    var editor = new DnDJTree.Editor(this, renderer, params)
	
	addTreeSelectionListener(this)
	model.addTreeModelListener(this)
    setCellRenderer(renderer)
    setEditable(true)
    setCellEditor(editor)
    editor.addCellEditorListener(this)
	setShowsRootHandles(true)
	
	setDragEnabled(true)
	setDropMode(DropMode.INSERT)
	setTransferHandler(new DnDJTree.TransferHandler(this))
	
	//dgr.setSourceActions(dgr.getSourceActions() & ~InputEvent.BUTTON3_MASK)
	/*private var dragSource = new DragSource() {
		override def createDragSourceContext(
				dscp:DragSourceContextPeer, 
				dgl:DragGestureEvent, 
				dragCursor:Cursor,
				dragImage:Image, 
				imageOffset:Point, 
				t:Transferable,
				dsl:DragSourceListener
		):DragSourceContext = {
			return new DragSourceContext(dscp, dgl, dragCursor,
										dragImage, imageOffset, t, dsl) {
				override def updateCurrentCursor(
						dropOp:Int, targetAct:Int, status:Int) = {}
			}
		}
	}*/
	
	
	
	/** DragGestureListener interface method */
	def dragGestureRecognized(e:DragGestureEvent) = {
		//println("in DragGestureListener.dragGestureRecognized")
		/*if (selectedPath != null) {
			var transferable:Transferable = model.getTransferable(selectedPath)
			var cursor = DragSource.DefaultCopyNoDrop
			if (e.getDragAction == DnDConstants.ACTION_MOVE) 
				cursor = DragSource.DefaultMoveNoDrop
			dragSource.startDrag(e, cursor, transferable, this)
		}*/
	}
	
	
	
	
	/** DragSourceListener interface method */
	def dragDropEnd(dsde:DragSourceDropEvent) {println("in DragSourceListener.dragDropEnd success="+dsde.getDropSuccess())}
	def dragEnter(dsde:DragSourceDragEvent) {
		println("in DragSourceListener.dragEnter")
		/*var context = dsde.getDragSourceContext
		var action = dsde.getDropAction
		updateCursor(action, context, 
				model.getTransferable(dropTargetPath), 
				model.getTransferable(selectedPath))*/
	}
	def dragOver(dsde:DragSourceDragEvent) {}
	def dropActionChanged(dsde:DragSourceDragEvent) {println("in DragSourceListener.dropActionChanged")}
	def dragExit(dsde:DragSourceEvent) {println("in DragSourceListener.dragExit")}
	
	
	
	
	/** DropTargetListener interface method - What we do when drag is released */
	def drop(e:DropTargetDropEvent):Unit = {
		println("in DropTargetListener.drop")
		/*try {
			var tr = e.getTransferable

			var addTree:Boolean = 
				tr.isDataFlavorSupported(TransferableTTree.DATA_FLAVOR)
			
			var data = getData(tr)
			if (data == null) reject(e, "DataFlavor not supported")
	
			var loc:Point 			= e.getLocation
			var destinationPath 	= getPathForLocation(loc.x, loc.y)
			
			val msg = testDropTarget(destinationPath, data, selectedPath)
			if (msg != null) {
				reject(e, "drop rejected: "+msg)
				return
			}
	
			var newParentPath 	= destinationPath
			var oldParentPath 	= selectedPath.getParentPath
			var action:Int 		= e.getDropAction
			var copyAction 		= (action == DnDConstants.ACTION_COPY)
	
			try { 
				if (!copyAction) {
					model.moveNode(selectedPath, newParentPath)
					e.acceptDrop(DnDConstants.ACTION_MOVE)
				} else {
					if (addTree) model.addTree(
							data.asInstanceOf[TTree[Transferable]], 
							newParentPath)
					else model.addNode(
							data.asInstanceOf[Transferable], 
							newParentPath)
					e.acceptDrop(DnDConstants.ACTION_COPY)
				}
			} catch {
				case ils:IllegalStateException =>
					reject(e, "ERROR: "+ils.getMessage)
			}

			e.getDropTargetContext.dropComplete(true)
			expandPath(destinationPath)
		} catch {
			case io:IOException => 
				reject(e, "ERROR: "+io.getMessage)
			case ufe:UnsupportedFlavorException => 
				reject(e, "ERROR: "+ufe.getMessage)
		}*/
	}
	
	
	
		
	/** DropTargetListener interface */
	def dropActionChanged(e:DropTargetDragEvent) {}//= println("in DropTargetListener.dropActionChanged")
	def dragEnter(e:DropTargetDragEvent) {}//= println("in DropTargetListener.dragEnter")
	def dragExit(e:DropTargetEvent) {}//= println("in DropTargetListener.dragExit")
	def dragOver(e:DropTargetDragEvent) = {
		//println("in DropTargetListener.dragOver")
		/*var cursorLocationBis:Point = e.getLocation
	    dropTargetPath = 
	    	getPathForLocation(cursorLocationBis.x, cursorLocationBis.y)
	
	    // if destination path is okay accept drop...
	    var data = getData(e.getTransferable)
	    if (testDropTarget(dropTargetPath, data, selectedPath) == null)
	    	e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE)
	    else
	    	e.rejectDrag*/
	}
	
	
	
	
	/** CellEditorListener */
	def editingCanceled(e:ChangeEvent) = {
		//println("TreeCellEditorListener.editingCanceled")
	}
	def editingStopped(e:ChangeEvent) = {
		//println("TreeCellEditorListener.editingStopped")
		params.updateObj(model.getTransferable(selectedPath), editor.text)
		model.reload(selectedPath)
	}
	
	
	
	
	
	/** TreeSelectionListener - sets selected node */
	def valueChanged(evt:TreeSelectionEvent) {
		//println("in TreeSelectionListener.valueChanged")
	    selectedPath = evt.getNewLeadSelectionPath
	}
	
	
	
	
	/** TreeModelListener - sets selected node */
	def treeNodesChanged(e:TreeModelEvent):Unit = {println("TreeModelListener.treeNodesChanged")}
	def treeNodesInserted(e:TreeModelEvent):Unit = {println("TreeModelListener.treeNodesInserted")}
	def treeNodesRemoved(e:TreeModelEvent):Unit = {println("TreeModelListener.treeNodesRemoved")}
	def treeStructureChanged(e:TreeModelEvent):Unit = {println("TreeModelListener.treeStructureChanged")}
	
	
	
	
	
	////////////// private convenience methods /////////////////
	
	private def getSelected = model.getTransferable(selectedPath)
	
	private def updateCursor(
						action:Int, 
						context:DragSourceContext, 
						target:Transferable,
						dragged:Transferable
	) = {
		action match {
			case DnDConstants.ACTION_MOVE => {
				context.setCursor(
					if (params.relationOk(dragged, target))	
						DragSource.DefaultMoveDrop
					else
						DragSource.DefaultMoveNoDrop
				)
			}
			case DnDConstants.ACTION_COPY => {
				context.setCursor(
					if (params.relationOk(dragged, target))
						DragSource.DefaultCopyDrop
					else
						DragSource.DefaultCopyNoDrop
				)
			}
			case _ => {}
		}
	}
	
	private def reject(e:DropTargetDropEvent, s:String) = {
		e.rejectDrop
		//println("drop rejected: "+s)
	}
	
	private def getData(tr:Transferable):AnyRef = {
		if (tr.isDataFlavorSupported(TransferableTTree.DATA_FLAVOR))
			tr.getTransferData(TransferableTTree.DATA_FLAVOR)
		else 
			params.supportedDataFlavors.find(df =>
					tr.isDataFlavorSupported(df)
				) match {
					case Some(df) => tr.getTransferData(df)
					case None => null
				}
	}
}