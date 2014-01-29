package se.lth.immun.collection

import java.io.IOException
import java.io.Serializable
import java.awt.datatransfer._

object AnyHolder {
	val DATA_FLAVOR = new DataFlavor(
							classOf[AnyHolder], 
							DataFlavor.javaJVMLocalObjectMimeType)
}

trait AnyHolder extends Transferable with Serializable with AnyGiver {
	var obj:Any
	def any = obj
	
	/** Transferable interface */
	import AnyHolder._
	def getTransferDataFlavors() = Array(DATA_FLAVOR)
	def isDataFlavorSupported(df:DataFlavor) = df == DATA_FLAVOR

	@throws(classOf[UnsupportedFlavorException])
	def getTransferData(df:DataFlavor):AnyRef = {
		if (df == DATA_FLAVOR) return this
		else throw new UnsupportedFlavorException(df)
	}


	/** Serializable interface */

	@throws(classOf[IOException])
	private def writeObject(out:java.io.ObjectOutputStream) = 
		out.defaultWriteObject()

	@throws(classOf[IOException])
	@throws(classOf[ClassNotFoundException])
	private def readObject(in:java.io.ObjectInputStream) = 
		in.defaultReadObject()
}

trait AnyGiver {
	def any:Any
}
