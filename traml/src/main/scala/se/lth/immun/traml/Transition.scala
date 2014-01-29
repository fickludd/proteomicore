package se.lth.immun.traml

import se.lth.immun.xml.XmlReader
import se.lth.immun.xml.XmlWriter
import scala.collection.mutable.ArrayBuffer

object Transition {
	
	import TraML._
	
	def fromFile(r:XmlReader):Transition = {
		var x = new Transition
		var e = r.top
		
		x.id = r.readAttribute(ID)
		x.compoundRef = r.readOptional(COMPOUND_REF)
		x.peptideRef = r.readOptional(PEPTIDE_REF)

		r.next
		while (r.in(e))
			r.top.name match {
				case CV_PARAM => 
					x.cvParams += CvParam.fromFile(r)
				case USER_PARAM => 
					x.userParams += UserParam.fromFile(r)
				case PRECURSOR => 
					x.precursor = Precursor.fromFile(r)
				case INTERMEDIATE_PRODUCT => 
					x.intermediateProducts += IntermediateProduct.fromFile(r)
				case PRODUCT => 
					x.product = Product.fromFile(r)
				case RETENTION_TIME => 
					x.retentionTime = Some(RetentionTime.fromFile(r))
				case PREDICTION => 
					x.prediction = Some(Prediction.fromFile(r))
				case _ => r.skipThis
			}
		
		return x
	}
}

class Transition {
	var compoundRef:Option[String] = None
	var id:String = null
	var peptideRef:Option[String] = None
	
	var precursor:Precursor = null
	var intermediateProducts = new ArrayBuffer[IntermediateProduct]
	var product:Product = null
	var retentionTime:Option[RetentionTime] = None
	var prediction:Option[Prediction] = None
	
	var cvParams = new ArrayBuffer[CvParam]
	var userParams = new ArrayBuffer[UserParam]
	
	
	def write(w:XmlWriter) = {
		import TraML._
		
		w.startElement(TRANSITION)
		w.writeAttribute(ID, id)
		w.writeOptional(COMPOUND_REF, compoundRef)
		w.writeOptional(PEPTIDE_REF, peptideRef)
		
		cvParams.foreach(x => x.write(w))
		userParams.foreach(x => x.write(w))
		
		precursor.write(w)
		intermediateProducts.foreach(_.write(w))
		product.write(w)
		if (retentionTime.isDefined)
			retentionTime.get.write(w)
		if (prediction.isDefined)
			prediction.get.write(w)
		
		w.endElement
	}
}