package se.lth.immun

import java.io._
import scala.io.Source
import scala.collection.mutable.ArrayBuffer
import scala.util.{Try, Success, Failure}

import org.apache.commons.math3.stat.regression.SimpleRegression
import org.apache.commons.math3.stat.regression.RegressionResults
import org.apache.commons.math3.stat.StatUtils

import java.awt.Graphics2D
import java.awt.Color
import java.awt.BasicStroke
import se.jt.{ ScatterPlot, LinePlot, Util, Plot, Geom, Scale, PixelSpace }
import Geom.Rect
import javax.imageio.ImageIO
import java.io.IOException

object IRT {

	case class IRTPeptide(sequence:String, iRT:Double)
	case class IRTDataPoint(sequence:String, iRT:Double, rt:Double, intensity:Double)
	case class IRTPrediction(predIRT:Double, dp:IRTDataPoint) {
		def residual = predIRT - dp.iRT
	}
	case class Regression(sr:SimpleRegression, rr:RegressionResults) {
		def predict(x:Double) = x * sr.getSlope + sr.getIntercept
		override def toString = "x * %f + %f".format(sr.getSlope, sr.getIntercept)
	}
	
	trait IRTMap {
		def dataPoints:Seq[IRTDataPoint]
		def anythingWrong(r2Limit:Double, nLimit:Int):Option[String]
		def predict(rt:Double):Double
		def toFile(f:File):Unit = {
			val w = new BufferedWriter(new FileWriter(f))
			writeMapParams(w.write)
			w.write(" %d datapoints:\n".format(dataPoints.length))
			w.write("peptide\tiRT\trt\tintensity\n")
			for (dp <- dataPoints)
				w.write("%s\t%f\t%f\t%f\n".format(dp.sequence, dp.iRT, dp.rt, dp.intensity))
			w.close
		}
		def writeMapParams(write:String => Unit):Unit
		def plot(f:File):Unit = {
			import Scale._
			val s = new ScatterPlot(dataPoints).x(_.rt).y(_.iRT)
			s.overlays += annotationPlot
			
			val imgCtrl = Util.drawToBuffer(800, 800, s)
			
			val w = new BufferedOutputStream(new FileOutputStream(f))
			try { 
			    ImageIO.write(imgCtrl.img, "png", w) 
			} catch {
				case ioe:IOException =>
			    	ioe.printStackTrace
			}
			w.close
		}
		def annotationPlot:Plot[IRTDataPoint, IRTDataPoint, IRTDataPoint, Seq[IRTDataPoint]] = new TracerAnnotPlot(this)
	}
	
	class LinearIRTMap(
			val slope:Double, 
			val intercept:Double, 
			val r2:Double, 
			val residualStd:Double, 
			val dataPoints:Seq[IRTDataPoint]
	) extends IRTMap{
		def predict(rt:Double) = rt * slope + intercept
			
		override def toString = 
			"LinearIRTMap(slope=%f, intercept=%f, r2=%f, resStd=%f, nDataPoints=%d)".format(slope, intercept, r2, residualStd, dataPoints.length)
		
		def anythingWrong(r2Limit:Double, nLimit:Int):Option[String] = {
			if (r2 < r2Limit)
				Some("iRT map r2=%f below required level %f".format(r2, r2Limit))
			else None
		}
		
		def writeMapParams(write:String => Unit) = {
			write("slope: %f\n".format(slope))
			write("intercept: %f\n".format(intercept))
			write("r2: %f\n".format(r2))
			write("residualStd: %f\n".format(residualStd))
		}
		
		//def annotationPlot = new LinearAnnotPlot(this)
	}
	
	
	class InterpolatingIRTMap(
			val anchorPoints:Seq[IRTDataPoint], 
			val dataPoints:Seq[IRTDataPoint]
	) extends IRTMap{
		
		lazy val extrapolationRegression = regressDataPoints(anchorPoints)
		
		override def toString = "InterpolatingIRTMap(n=%d)".format(anchorPoints.length)
		
		def anythingWrong(r2Limit:Double, nLimit:Int):Option[String] = {
			if (anchorPoints.length < nLimit)
				Some("iRT map n anchors=%d below required level %d".format(anchorPoints.length, nLimit))
			else None
		}
		def predict(rt:Double) = 
			if (rt < anchorPoints.head.rt)
				extrapolate(rt, anchorPoints.head)
			else if (rt >= anchorPoints.last.rt)
				extrapolate(rt, anchorPoints.last)
			else 
				interpolate(rt)
		
		def extrapolate(rt:Double, adjust:IRTDataPoint) =
			extrapolationRegression.predict(rt) - extrapolationRegression.predict(adjust.rt) + adjust.iRT
				
		def interpolate(rt:Double):Double = {
			val i = anchorPoints.indexWhere(_.rt > rt)
			val lowDP = anchorPoints(i-1)
			val highDP = anchorPoints(i)
			val k = (rt - lowDP.rt) / (highDP.rt - lowDP.rt)
			k * (highDP.iRT - lowDP.iRT) + lowDP.iRT
		}
			
		def writeMapParams(write:String => Unit) = {
			write("  ANCHORS:\n")
			write("peptide\trt\tiRT\n")
			for (dp <- anchorPoints)
				write(" %s\t%f\t%f\n".format(dp.sequence, dp.rt, dp.iRT))
		}
	}
	
	
	
	class TracerAnnotPlot(
			irtMap:IRTMap
	) extends Plot[IRTDataPoint, IRTDataPoint, IRTDataPoint, Seq[IRTDataPoint]] {
		
		type Self = LinearAnnotPlot
		
		val nSAMPLES = 100
		
		def checkAndSetup(data: Seq[IRTDataPoint]) = (null, null, data)
		def data:Seq[IRTDataPoint] = Nil
		def renderData(
				g:Graphics2D, 
				r:Rect, 
				xScale:Scale[IRTDataPoint], 
				yScale:Scale[IRTDataPoint],
				data:Seq[IRTDataPoint]
		) = {
			val ps = new PixelSpace(r)
			
			val minRT = data.minBy(_.rt).rt
			val maxRT = data.maxBy(_.rt).rt
			
			g.setColor(Color.RED)
			for (i <- 0 to nSAMPLES) yield {
				val rt = minRT + (maxRT - minRT) * i / nSAMPLES
				val dp = IRTDataPoint("", irtMap.predict(rt), rt, 1.0)
				g.drawRect(ps.toX(xScale(dp, 0)), ps.toY(yScale(dp, 0)), 1, 1)
			}
		
			g.drawString(irtMap.toString, 10, 20)
		}
	}
	
	
	
	class LinearAnnotPlot(
			irtMap:LinearIRTMap
	) extends Plot[IRTDataPoint, IRTDataPoint, IRTDataPoint, Seq[IRTDataPoint]] {
		
		type Self = LinearAnnotPlot
		
		def checkAndSetup(data: Seq[IRTDataPoint]) = (null, null, data)
		def data:Seq[IRTDataPoint] = Nil
		def renderData(
				g:Graphics2D, 
				r:Rect, 
				xScale:Scale[IRTDataPoint], 
				yScale:Scale[IRTDataPoint],
				data:Seq[IRTDataPoint]
		) = {
			val ps = new PixelSpace(r)
			
			val min = data.minBy(_.rt)
			val max = data.maxBy(_.rt)
			val minDP = IRTDataPoint(min.sequence, min.rt * irtMap.slope + irtMap.intercept, min.rt, min.intensity)
			val maxDP = IRTDataPoint(max.sequence, max.rt * irtMap.slope + irtMap.intercept, max.rt, max.intensity)
			
			g.setColor(Color.RED)
			g.drawLine(
					ps.toX(xScale(minDP, 0)), 
					ps.toY(yScale(minDP, 0)),
					ps.toX(xScale(maxDP, 0)), 
					ps.toY(yScale(maxDP, 0))
				)
				
			g.drawString(irtMap.toString, 10, 20)
		}
	}
	
	
	
	
	def readPeptideTsv(f:File):Seq[IRTPeptide] = {
		
		var iPEPTIDE = -1
		var iIRT = -1
		
		var headerParsed = false
		val irtPeps = new ArrayBuffer[IRTPeptide]
		
		for ( line <- Source.fromFile(f).getLines ) {
			val p = line.split("\t")
			
			if (!headerParsed) {
				val lc = p.map(_.toLowerCase) 
				if ((lc.contains("peptide") || lc.contains("sequence")) && lc.contains("irt")) {
					iPEPTIDE = lc.indexOf("peptide")
					if (iPEPTIDE < 0) 
						iPEPTIDE = lc.indexOf("sequence")
					iIRT = lc.indexOf("irt")
				} else {
					iPEPTIDE = p.indexWhere(x => Try(x.toDouble).isFailure)
					iIRT = p.indexWhere(x => Try(x.toDouble).isSuccess)
					if (iPEPTIDE < 0 || iIRT < 0)
						throw new Exception("Could not parse IRT peptide definition file. Needs peptide sequence and IRT columns!")
					irtPeps += IRTPeptide(p(iPEPTIDE), p(iIRT).toDouble)
				}
				headerParsed = true
			} else
				irtPeps += IRTPeptide(p(iPEPTIDE), p(iIRT).toDouble)
		}
		
		irtPeps
	}
	
	
	
	def simpleRegressionMap(dataPoints:Seq[IRTDataPoint]):IRTMap = {
		val reg = regressDataPoints(dataPoints)
		new LinearIRTMap(
				reg.sr.getSlope,
				reg.sr.getIntercept,
				reg.rr.getRSquared,
				math.sqrt(reg.rr.getMeanSquareError),
				dataPoints
			)
		
	}
	
	
	def robustRegressionMap(dataPoints:Seq[IRTDataPoint]):IRTMap = {
		var r2 = 0.0
		var reg = regressDataPoints(dataPoints)
		
		while (reg.rr.getRSquared > r2 + 0.01) {
			//println(reg)
			val predictions = dataPoints.map(x => IRTPrediction(reg.predict(x.rt), x))
			val cleanedDataPoints = 
				predictions.sortBy(p => -math.abs(p.residual)).tail.map(_.dp)
			r2 = reg.rr.getRSquared
			reg = regressDataPoints(cleanedDataPoints)
		}
				
		println(reg)
		
		new LinearIRTMap(
				reg.sr.getSlope,
				reg.sr.getIntercept,
				reg.rr.getRSquared,
				math.sqrt(reg.rr.getMeanSquareError),
				dataPoints
			)
	}
	
	
	def medianInterpolationMap(dataPoints:Seq[IRTDataPoint]):IRTMap = {
		val medianDataPoints = dataPoints.groupBy(_.sequence).values.map(dps => {
				val sorted = dps.sortBy(_.rt)
				sorted(dps.length / 2)
			}).toSeq.sortBy(_.rt)
		
		new InterpolatingIRTMap(medianDataPoints, dataPoints)
	}
	
	
	def weightedMeanInterpolationMap(dataPoints:Seq[IRTDataPoint]):IRTMap = {
		val wMeanDataPoints = dataPoints.groupBy(_.sequence).values.map(dps => {
				val wSum = dps.map(_.intensity).sum
				val rtSum = dps.map(dp => dp.rt * dp.intensity).sum
				IRTDataPoint(dps.head.sequence, dps.head.iRT, rtSum / wSum, wSum)
			}).toSeq.sortBy(_.rt)
		
		new InterpolatingIRTMap(wMeanDataPoints, dataPoints)
	}
	
	
	def regressDataPoints(dataPoints:Seq[IRTDataPoint]) = {
		val sr = new SimpleRegression(true)
		for (dp <- dataPoints)
			sr.addData(dp.rt, dp.iRT)
		val rr = sr.regress
		Regression(sr, rr)
	}
}