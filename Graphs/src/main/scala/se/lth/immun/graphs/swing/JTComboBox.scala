package se.lth.immun.graphs.swing

import swing._
import swing.event._

import javax.swing.{JComboBox, ComboBoxModel, AbstractListModel, JList, ListCellRenderer}
import javax.swing.event.{ListDataListener, ListDataEvent}

import scala.collection.mutable.ArrayBuffer

object JTComboBox {
	
	
	class Model[T](
		items:Seq[T]
	) extends AbstractListModel with ComboBoxModel {
		
		var ts = new ArrayBuffer[T]()
		private var selected:Option[T] = None 
		setItems(items)
			
		def getSelectedItem:AnyRef = selected
		
		def setSelectedItem(a: Any) {
			a.asInstanceOf[Option[T]] match {
				case None => if (!selected.isEmpty) {
					selected = None
					fireContentsChanged(this, -1, -1)
				}
				case Some(t) => if (selected.isEmpty || selected.get != t) {
					selected = Some(t)
					fireContentsChanged(this, -1, -1)
				}
			}
		}
		
		def getElementAt(n: Int):AnyRef = {
			if (n >= 0 && n < ts.length) 	Some(ts(n))
			else							None
		}
			
		def getSize = Math.max(1, ts.length)
		
		def setItems(items:Seq[T]) = {
			ts.clear
			for (t <- items) ts += t
			selected = 	if (ts.isEmpty) 	None 
						else 				Some(ts(0))
			fireContentsChanged(this, -1, -1)
		}
		
		def addItem(t:T) = {
			if (!ts.contains(t)) {
				ts += t
				fireContentsChanged(this, -1, -1)
			}
		}
		
		def removeItem(t:T) = {
			if (ts.contains(t)) {
				ts -= t
				fireContentsChanged(this, -1, -1)
			}
		}
	}
}





class JTComboBox[T](
		emptyString:String = "",
		items:Seq[T] = Nil
) extends Component with Publisher {
	override lazy val peer: JComboBox = new JComboBox(
			new JTComboBox.Model(items)) with SuperMixin
			
	var model = peer.getModel.asInstanceOf[JTComboBox.Model[T]]
	model.addListDataListener(new ListDataListener {
		def contentsChanged(e:ListDataEvent) = {
			peer.setEnabled(!model.ts.isEmpty)
		}
		def intervalAdded(e:ListDataEvent) = {}
		def intervalRemoved(e:ListDataEvent) = {}
	})
	
	maximumSize = new Dimension(2000, 20)
	import ListView.Renderer
	private var defaultRenderer = new javax.swing.plaf.basic.BasicComboBoxRenderer.UIResource()
	def renderer:Renderer[T] = Renderer.wrap(peer.getRenderer)
	def renderer_=(r: Renderer[T]) { 
		peer.setRenderer(new ListCellRenderer {
			def getListCellRendererComponent(
					list:JList, 
					a:Any, 
					index:Int, 
					isSelected:Boolean, 
					focused:Boolean
			) = {
				a.asInstanceOf[Option[T]] match {
				case Some(t) => r.componentFor(ListView.wrap[T](list), isSelected, 
											focused, t, index).peer
				case None => defaultRenderer.getListCellRendererComponent(
											list, emptyString, index, isSelected, focused)
			}}
		}) 
	}
	renderer = Renderer(_ + "")
	
	
	object selection extends Publisher {
		def index:Int 			= peer.getSelectedIndex
		def index_=(n: Int) 	{ peer.setSelectedIndex(n) }
		def item:Option[T] 		= peer.getSelectedItem.asInstanceOf[Option[T]]
		def item_=(t:T) 		{ peer.setSelectedItem(Some(t)) }
		def item_=(st:Option[T]) { peer.setSelectedItem(st) }
		peer.addActionListener(Swing.ActionListener { e =>
			publish(event.SelectionChanged(JTComboBox.this))
		})
	}
	
	
	def prototypeDisplayValue:Option[T] = 
			peer.getPrototypeDisplayValue match {
				case t:T => Some(t)
				case _ => None
			}
	def prototypeDisplayValue_=(v:Option[T]) { 
		peer.setPrototypeDisplayValue(v.map(_.asInstanceOf[AnyRef]).orNull)
	}
}