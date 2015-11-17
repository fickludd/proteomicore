package se.lth.immun.protocol

import java.io.File
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.BufferedOutputStream
import java.io.FileOutputStream

import se.lth.immun.protocol.MSFragmentationProtocol._

import com.google.protobuf.CodedInputStream

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer



trait FragmentAnnotation {
	def base:BaseFragment
}
case class BaseFragment(
		val intensity:Double,
		val z:Int,
		val mz:Option[Double],
		val intensityStd:Option[Double],
		val mzErrPPM:Option[Double],
		val n:Int
	) {
	
	def normalizeBy(base:Double) =
		BaseFragment(intensity / base, z, mz, intensityStd.map(_ / base), mzErrPPM, n)
}

case class SimpleFragment(
		val base:BaseFragment,
		val fragmentType:FragmentType,
		val ordinal:Int
	) extends FragmentAnnotation

case class XLinkFragment(
		val base:BaseFragment,
		val fragmentType:FragmentType,
		val ordinal:Int,
		val peptide:Int
	) extends FragmentAnnotation
	
case class InternalFragment(
		val base:BaseFragment,
		val firstIndex:Int,
		val lastIndex:Int
	) extends FragmentAnnotation

case class Observation(
		val fragmentationType:FragmentationType,
		val z:Int,
		val ce:Double,
		val precursorMz:Option[Double],
		val precursorIntensity:Option[Double],
		val iRT:Option[Double],
		val iRTstd:Option[Double],
		val fragBaseIntensity:Option[Double],
		val qValue:Option[Double],
		val percentAnnotatedOfMS2tic:Option[Double],
		val n:Option[Int],
		val precursorType:Option[PrecursorType],
		val precursorIntensityRank:Option[Int],
		val precursorFeatureApexIntensity:Option[Double],
		val score:Option[Double],
		val fragments:Seq[FragmentAnnotation])
		
case class AAMolecule(
		val fileIndex:Int,
		val sequence:String,
		val protein:String,
		val mass:Double,
		val observations:Seq[Observation])
	

object MsFragmentationFile {

	def read(f:File, verbose:Boolean) = {
		val r = new ProtoBufferedFileInputStream(2048*2048, f)
		
		if (!parseAndCodeSize(r, verbose))
			throw new Exception("Error reading MsgSize!")
		
		val aaMolecules = new ArrayBuffer[AAMolecule]
		var i = 0
		while (parseAndCodeSize(r, verbose)) {
			aaMolecules += readAAMolecule(r, i)
			i += 1
		}
		
		aaMolecules
	}
	
	def readAAMolecule(
			r:ProtoBufferedFileInputStream, 
			fileIndex:Int
	):AAMolecule = {
		val f = MSFragmentationProtocol.AAMolecule.parseFrom(r.cis)
		AAMolecule(
			fileIndex,
			f.getSequence,
			f.getProtein,
			f.getMass, 
			f.getObservationList.map(toObservation)
		)
	}
	
	def toObservation(obs:MSFragmentationProtocol.Observation):Observation = 
		Observation(
				obs.getType,
				obs.getCharge,
				obs.getCe,
				if (obs.hasPrecursorMz) 		Some(obs.getPrecursorMz) 		else None,
				if (obs.hasPrecursorIntensity) 	Some(obs.getPrecursorIntensity) else None,
				if (obs.hasIRT) 				Some(obs.getIRT) 				else None,
				if (obs.hasIRTstd) 				Some(obs.getIRTstd)				else None,
				if (obs.hasFragmentBaseIntensity) 	Some(obs.getFragmentBaseIntensity) 		else None,
				if (obs.hasQValue) 					Some(obs.getQValue) 					else None,
				if (obs.hasPercentAnnotatedOfMS2Tic) Some(obs.getPercentAnnotatedOfMS2Tic) 	else None,
				if (obs.hasN) 						Some(obs.getN) 							else None,
				if (obs.hasPrecursorType) 			Some(obs.getPrecursorType) 				else None,
				if (obs.hasPrecursorIntensityRank) 	Some(obs.getPrecursorIntensityRank) 	else None,
				if (obs.hasPrecursorFeatureApexIntensity) Some(obs.getPrecursorFeatureApexIntensity) else None,
				if (obs.hasScore) Some(obs.getScore) else None,
				obs.getFragmentList.map(toFragment))
	
	
	def toFragment(f:MSFragmentationProtocol.Fragment):FragmentAnnotation = {
		f.getType match {
			case FragmentType.M =>
				InternalFragment(toBaseFragment(f), f.getInternalFirst, f.getInternalLast)
			case t =>
				if (f.hasOrigPeptide)
					XLinkFragment(toBaseFragment(f), f.getType, f.getOrdinal, f.getOrigPeptide)
				else
					SimpleFragment(toBaseFragment(f), f.getType, f.getOrdinal)
		}
	}
	
	def toBaseFragment(f:MSFragmentationProtocol.Fragment):BaseFragment = 
		BaseFragment(
				f.getIntensity,
				f.getCharge,
				if (f.hasMz) Some(f.getMz) else None,
				if (f.hasIntensityStd) Some(f.getIntensityStd) else None,
				if (f.hasMzErrPPM) Some(f.getMzErrPPM) else None,
				f.getN
			)
	
	def parseAndCodeSize(r:ProtoBufferedFileInputStream, verbose:Boolean) = {
		if (r.ensure(5)) {
			val n = MsgSize.parseFrom(r.cis).getSize
			if (verbose)
				println("reading msg of n=%d bytes".format(n))
			r.ensure(n)
		} else false
	}
	
	def write(f:File, aaMolecules:Seq[AAMolecule], verbose:Boolean) = {
		val w = new BufferedOutputStream(new FileOutputStream(f))
		for (x <- aaMolecules) {
			val aaMolMsg = buildAAMolecule(x)
			writeMsg(w, aaMolMsg.toByteArray, verbose)
			if (verbose)
				println(aaMolMsg.toString)
		}
		w.close
	}
	
	def buildAAMolecule(x:AAMolecule):MSFragmentationProtocol.AAMolecule = {
		val b = MSFragmentationProtocol.AAMolecule.newBuilder
			.setSequence(x.sequence)
			.setProtein(x.protein)
			.setMass(x.mass)
		for (obs <- x.observations) b.addObservation(buildObservation(obs))
		b.build
	}
	
	def buildObservation(obs:Observation):MSFragmentationProtocol.Observation = {
		val b = MSFragmentationProtocol.Observation.newBuilder
			.setType(obs.fragmentationType)
			.setCharge(obs.z)
			.setCe(obs.ce.toFloat)
		obs.precursorMz.foreach(b.setPrecursorMz(_))
		obs.precursorIntensity.foreach(x => b.setPrecursorIntensity(x.toFloat))
		obs.iRT.foreach(x => b.setIRT(x.toFloat))
		obs.iRTstd.foreach(x => b.setIRTstd(x.toFloat))
		obs.fragBaseIntensity.foreach(x => b.setFragmentBaseIntensity(x.toFloat))
		obs.qValue.foreach(x => b.setQValue(x.toFloat))
		obs.percentAnnotatedOfMS2tic.foreach(x => b.setPercentAnnotatedOfMS2Tic(x.toFloat))
		obs.n.foreach(b.setN(_))
		obs.precursorType.foreach(b.setPrecursorType(_))
		obs.precursorIntensityRank.foreach(b.setPrecursorIntensityRank(_))
		obs.precursorFeatureApexIntensity.foreach(x => b.setPrecursorFeatureApexIntensity(x.toFloat))
		obs.score.foreach(x => b.setScore(x.toFloat))
		for (f <- obs.fragments) b.addFragment(buildFragment(f))
		b.build
	}
	
	def buildFragment(f:FragmentAnnotation):Fragment = {
		val b = Fragment.newBuilder
		b.setIntensity(f.base.intensity.toFloat)
		b.setCharge(f.base.z)
		f.base.mz.foreach(x => b.setMz(x))
		f.base.intensityStd.foreach(x => b.setIntensityStd(x.toFloat))
		if (f.base.n != 1)
			b.setN(f.base.n)
		
		f match {
			case SimpleFragment(base, ftype, ord) =>
				b.setType(ftype)
					.setOrdinal(ord)
				
			case XLinkFragment(base, ftype, ord, pep) =>
				b.setType(ftype)
					.setOrdinal(ord)
					.setOrigPeptide(pep)
				
			case InternalFragment(base, first, last) =>
				b.setType(FragmentType.M)
					.setInternalFirst(first)
					.setInternalLast(last)
				
		}
		
		b.build
	}
	
	def writeMsg(w:BufferedOutputStream, bytes:Array[Byte], verbose:Boolean = false) = {
		val n = bytes.length
		val sizeMsgBytes = MsgSize.newBuilder().setSize(n).build.toByteArray
		if (verbose)
			println("writing msg, n(bytes): %d+%d".format(sizeMsgBytes.length, n))
		w.write(sizeMsgBytes)
		w.write(bytes)
	}
}