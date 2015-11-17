package se.lth.immun.traml.clear

import scala.collection.mutable.ArrayBuffer
import se.lth.immun.traml.CvParam

object Clear {
	
	val PROTEIN_ACCESSION_ACC = "MS:1000885"
	val PROTEIN_SHORT_NAME_ACC = "MS:1000883"
	val PROTEIN_NAME_ACC = "MS:1000886"
		
	val HEAVY_LABELLED_PEPTIDE_ACC = "MS:1000891"
	val ISOLATION_WINDOW_TARGET_ACC = "MS:1000827"
	val CHARGE_STATE_ACC = "MS:1000041"
	val COLLISION_ENERGY_ACC = "MS:1000045"
	val PRODUCT_ION_INTENSITY_ACC = "MS:1001226"
	val PRECURSOR_ION_INTENSITY_ACC = "MS:1001141"
		
	val NORMALIZED_RETENTION_TIME_ACC = "MS:1000896"
	val LOCAL_RETENTION_TIME_ACC = "MS:1000895"
	val RT_WINDOW_LOWER_OFFSET_ACC = "MS:1000916"
	val RT_WINDOW_UPPER_OFFSET_ACC = "MS:1000917"
	val IRT_NORM_STANDARD_ACC = "MS:1002005"
		
	val FRAG_Y_SERIES_ACC = "MS:1001220"
	val FRAG_B_SERIES_ACC = "MS:1001222"
	val FRAG_ORDINAL_ACC = "MS:1000903"
		
	val CHARGE = "MS:1000041"
	val OPENSWATH_UPARAM_ANNOTATION = "annotation"
	val FULL_PEPTIDE_NAME = "full_peptide_name"
	
	val PEPTIDE_GROUP_LABEL = "MS:1000893"
		
	case class Channel(mz:Double, z:Int, id:String, msLevel:Int, expIntensity:Option[Double])
	
	class Assay(
			val mz:Double,
			val z:Int,
			val parentId:String,
			val ce:Option[Double]
	) {
		val ms1Channels = new ArrayBuffer[Channel]
		val ms2Channels = new ArrayBuffer[Channel]
		override def equals(o:Any) = 
			o match {
				case that: Assay => that.parentId == this.parentId && that.z == this.z
			    case _ => false
			}
		override def hashCode = parentId.hashCode + z.hashCode
		def metaCopy = new Assay(mz, z, parentId, ce)
	}
	
	
	def cvDouble(acc:String, cvParams:Seq[CvParam]) =
		cvParams.find(_.accession == acc).flatMap(_.value.map(_.toDouble))
	def cvInt(acc:String, cvParams:Seq[CvParam]) =
		cvParams.find(_.accession == acc).flatMap(_.value.map(_.toInt))
}