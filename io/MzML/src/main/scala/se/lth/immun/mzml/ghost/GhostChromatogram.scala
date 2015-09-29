package se.lth.immun.mzml.ghost

import se.lth.immun.mzml._
import se.lth.immun.base64.Base64
import java.util.zip.Inflater

object GhostChromatogram {
	
	
	def fromChromatogram(c:Chromatogram) = {
		var gc = new GhostChromatogram(c)
		
		gc.id = c.id
		for (bda <- c.binaryDataArrays)
			gc.readBinaryArray(bda, c.defaultArrayLength)
					
		gc
	}
}

class GhostChromatogram(
		var chromatogram:Chromatogram	= new Chromatogram
) {
	
	case class BytesLength(bytes:Array[Byte], length:Int)
	
	import Ghost._
	import ms.numpress.MSNumpress
	import ms.numpress.MSNumpress._
	
	var id:String 					= null
	var times:Seq[Double] 			= null
	var intensities:Seq[Double] 	= null
	var timeDef:GhostBinaryDataArray.DataDef = null
	var intensityDef:GhostBinaryDataArray.DataDef = null
	
	
	def readBinaryArray(b:BinaryDataArray, defaultArrayLength:Int):Unit = {
		val (dataDef, data) = GhostBinaryDataArray.read(b, defaultArrayLength)
		dataDef.dataType match {
			case GhostBinaryDataArray.Time() =>
				times = data
				timeDef = dataDef
			case GhostBinaryDataArray.Intensity() =>
				intensities = data
				intensityDef = dataDef
			case x =>
				throw new GhostException("Unknown or unsupported datatype in chromatogram: "+x)
		}
		b.binary = ""
	}
	
	
	
	def newMz(mz:Double, bound:Double = 0.0) = {
		class MzParam extends CvParam {
			cvRef 			= "MS"
			unitCvRef 		= Some("MS")
			unitAccession 	= Some("MS:1000040")
			unitName 		= Some("m/z")
		}
		
		var cv = new MzParam
		cv.accession 	= ISOLATION_WINDOW_TARGET
		cv.name 		= "isolation window target m/z"
		cv.value 		= Some(mz.toString)
		
		if (bound > 0.0) {
			var cvLow		= new MzParam
			cvLow.accession = ISOLATION_WINDOW_LOWER_OFFSET
			cvLow.name 		= "isolation window lower offset"
			cvLow.value 	= Some(bound.toString)
			
			var cvUpp		= new MzParam
			cvUpp.accession = ISOLATION_WINDOW_UPPER_OFFSET
			cvUpp.name 		= "isolation window upper offset"
			cvUpp.value 	= Some(bound.toString)
			List(cv, cvLow, cvUpp)
		} else List(cv)
	}
	
	
	
	def newCe(ce:Double) = {
		var cv = new CvParam
		cv.cvRef 		= "MS"
		cv.accession 	= COLLISION_ENERGY
		cv.name 		= "collision energy"
		cv.value 			= Some(ce.toString)
		cv.unitCvRef 		= Some("UO")
		cv.unitAccession 	= Some("UO:0000266")
		cv.unitName 		= Some("electronvolt")
		cv
	}
	
	
	
	def precursor:Double = {
		if (!chromatogram.precursor.isDefined) return -1.0
		val pc = chromatogram.precursor.get
		if (!pc.isolationWindow.isDefined) return -1.0
		val iw = pc.isolationWindow.get
		iw.cvParams.find(_.accession == ISOLATION_WINDOW_TARGET) match {
			case Some(cvParam) 	=> cvParam.value.get.toDouble
			case None 			=> -1.0
		}
	}
	
	
	
	def precursor_=(q1:Double) = {
		if (!chromatogram.precursor.isDefined) 
			chromatogram.precursor = Some(new Precursor{ activation = new Activation })
		val pc = chromatogram.precursor.get
		if (!pc.isolationWindow.isDefined) 
			pc.isolationWindow = Some(new IsolationWindow)
		val iw = pc.isolationWindow.get
		iw.cvParams.find(_.accession == ISOLATION_WINDOW_TARGET) match {
			case Some(cvParam) 	=> cvParam.value = Some(q1.toString)
			case None 			=> 
				iw.cvParams ++= newMz(q1)
		}
	}
	
	
	
	def product:Double = {
		if (!chromatogram.product.isDefined) return -1.0
		val pc = chromatogram.product.get
		if (!pc.isolationWindow.isDefined) return -1.0
		val iw = pc.isolationWindow.get
		iw.cvParams.find(_.accession == ISOLATION_WINDOW_TARGET) match {
			case Some(cvParam) 	=> cvParam.value.get.toDouble
			case None 			=> -1.0
		}
	}
	
	
	
	def product_=(q3:Double) = {
		if (!chromatogram.product.isDefined) 
			chromatogram.product = Some(new Product)
		val p = chromatogram.product.get
		if (!p.isolationWindow.isDefined) 
			p.isolationWindow = Some(new IsolationWindow)
		val iw = p.isolationWindow.get
		iw.cvParams.find(_.accession == ISOLATION_WINDOW_TARGET) match {
			case Some(cvParam) 	=> cvParam.value = Some(q3.toString)
			case None 			=> 
				iw.cvParams ++= newMz(q3, 0.01)
		}
	}
	
	
	
	def collisionEnergy:Double = {
		if (!chromatogram.precursor.isDefined) return -1.0
		val a = chromatogram.precursor.get.activation
		a.cvParams.find(_.accession == COLLISION_ENERGY) match {
			case Some(cvParam) 	=> cvParam.value.get.toDouble
			case None 			=> -1.0
		}
	}
	
	
	
	def collisionEnergy_=(ce:Double) = {
		if (!chromatogram.precursor.isDefined) 
			chromatogram.precursor = Some(new Precursor{ activation = new Activation })
		val a = chromatogram.precursor.get.activation
		a.cvParams.find(_.accession == COLLISION_ENERGY) match {
			case Some(cvParam) 	=> cvParam.value = Some(ce.toString)
			case None 			=> 
				a.cvParams += newCe(ce)
		}
	}
	
	
	
	def getId = 
			if (chromatogram.id != null) 	chromatogram.id 
			else if (id != null) 			id
			else "SRM SIC %.3f,%.3f".format(precursor, product)
	
	
	def toXChromatogram = 
		new XChromatogram(getId, precursor, product, collisionEnergy, intensities, times)
	
	
	
	def toChromatogram(index:Int):Chromatogram = {
		var c = new Chromatogram
		
		c.id 		= getId
		c.index 	= index
		c.defaultArrayLength = times.length
		c.cvParams += new CvParam {
			cvRef 		= "MS"
			accession 	= SRM_CHROM_ACC
			name 		= "selected reaction monitoring chromatogram"
		}
		c.precursor = chromatogram.precursor
		c.product 	= chromatogram.product
		
		c.binaryDataArrays += GhostBinaryDataArray.toBinaryDataArray(times, timeDef)
		c.binaryDataArrays += GhostBinaryDataArray.toBinaryDataArray(intensities, intensityDef)
		
		return c
	}
}