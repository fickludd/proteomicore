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
		val n:Int
	)

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
		val precursorMz:Double,
		val precursorIntensity:Double,
		val iRT:Double,
		val fragBaseIntensity:Double,
		val qValue:Double,
		val percentAnnotatedOfMS2tic:Double,
		val n:Int,
		val fragments:Seq[FragmentAnnotation])
		
case class AAMolecule(
		val fileIndex:Int,
		val sequence:String,
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
			f.getMass, 
			f.getObservationList.map(toObservation)
		)
	}
	
	def toObservation(obs:MSFragmentationProtocol.Observation):Observation = {
		val precMz =
			if (obs.hasPrecursorMz) obs.getPrecursorMz else 0.0
		val precIntensity =
			if (obs.hasPrecursorIntensity) obs.getPrecursorIntensity else 0.0
		val iRT =
			if (obs.hasIRT) obs.getIRT else -1.0
		val fragBaseIntensity =
			if (obs.hasFragmentBaseIntensity) obs.getFragmentBaseIntensity else -1.0
		val qValue =
			if (obs.hasQValue) obs.getQValue else -1.0
		val percentAnnotatedOfMS2tic =
			if (obs.hasPercentAnnotatedOfMS2Tic) obs.getPercentAnnotatedOfMS2Tic else -1.0
		Observation(
				obs.getType,
				obs.getCharge,
				obs.getCe,
				precMz,
				precIntensity,
				iRT,
				fragBaseIntensity,
				qValue,
				percentAnnotatedOfMS2tic,
				obs.getN,
				obs.getFragmentList.map(toFragment))
	}
	
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
			.setMass(x.mass)
		for (obs <- x.observations) b.addObservation(buildObservation(obs))
		b.build
	}
	
	def buildObservation(obs:Observation):MSFragmentationProtocol.Observation = {
		val b = MSFragmentationProtocol.Observation.newBuilder
			.setType(obs.fragmentationType)
			.setCharge(obs.z)
			.setCe(obs.ce)
			.setPrecursorMz(obs.precursorMz)
			.setIRT(obs.iRT)
			.setPrecursorIntensity(obs.precursorIntensity)
		for (f <- obs.fragments) b.addFragment(buildFragment(f))
		b.build
	}
	
	def buildFragment(f:FragmentAnnotation):Fragment = {
		val b = Fragment.newBuilder
		b.setIntensity(f.base.intensity)
		b.setCharge(f.base.z)
		f.base.mz.foreach(x => b.setMz(x))
		f.base.intensityStd.foreach(x => b.setIntensityStd(x))
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