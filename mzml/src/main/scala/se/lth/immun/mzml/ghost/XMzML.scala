package se.lth.immun.mzml.ghost

import se.lth.immun.xml.XmlReader
import se.lth.immun.mzml._

import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.BufferedReader
import java.util.Date
import java.util.Calendar
import java.nio.channels.FileChannel

object XMzML {
	
	def isIMzML(f:File) 	= f.getName.toLowerCase.endsWith(".imzml")
	def toIBD(f:File)		= new File(f.getAbsolutePath().dropRight(5) + "ibd")
	
	def getReaders(file:File):(XmlReader, File, FileChannel) = {
		val (binaryFile, binaryFileChannel) = 
			if (isIMzML(file)) {
				val binaryFile = toIBD(file)
				(binaryFile, new FileInputStream(binaryFile).getChannel())
			} else (null, null)
		(new XmlReader(new BufferedReader(new FileReader(file))), binaryFile, binaryFileChannel)
	}
	
	
	
	def fromFile(r:XmlReader, ignoreTIC:Boolean = true, binaryFileChannel:FileChannel = null):XMzML = {
		val x = new XMzML
		
		var dh = new MzMLDataHandlers(
				i => {}, 
				s => {}, 
				i => {}, 
				c => {
					if (!ignoreTIC || !c.cvParams.exists(_.accession == Ghost.TIC_ACC))
						x.grouper.add(GhostChromatogram.fromChromatogram(c).toXChromatogram)
				})
		x.mzml = MzML.fromFile(r, dh, binaryFileChannel)
		x.runTime = x.mzml.run.startTimeStamp match {
			case Some(str) =>
				try {
					parseTime(x.mzml.run.startTimeStamp.get)
				} catch {
					case e:Exception => new Date(0)
				}
			case None => new Date(0)
		}
		
		return x
	}
	
	
	
	private def parseTime(str:String):Date = {
		var split = str.split('T')//StringUtils.split(str, "T", 2)

		var dateStr = split(0)
		var dateSplit = dateStr.split('-')//StringUtils.split(dateStr, "-")
		var year = 		dateSplit(0).toInt
		var month = 	dateSplit(1).toInt - 1
		var day = 		dateSplit(2).toInt
		
		var timeStr = split(1)
		var timeSplit = timeStr.split(':')//StringUtils.split(timeStr, ':')
		var hour = 		timeSplit(0).toInt
		var minutes = 	timeSplit(1).toInt
		var second = 	timeSplit(2).substring(0, 2).toInt
		
		var cal = Calendar.getInstance
		cal.setLenient(false)
		cal.clear
		cal.set(year, month, day, hour, minutes, second)
		return cal.getTime
	}
	
	def main(args:Array[String]):Unit = {
		import java.io._
		
		//val f = new File("target/test-classes/numpressed.mzML")
		val f = new File("target/test-classes/"+args.head+".mzML")
		val x = XMzML.fromFile(new XmlReader(new BufferedReader(new FileReader(f))))
	}
}

class XMzML {
	
	var mzml:MzML 		= null
	var runTime:Date 	= null
	val grouper 		= new XChromatogramGrouper
}