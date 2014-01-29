package graphs.util

import org.scalatest.junit.AssertionsForJUnit

import scala.collection.mutable.ListBuffer
import se.lth.immun.graphs.util.IntRect;
import se.lth.immun.graphs.util.LogAxis;

import org.junit.Assert._
import org.junit.Test
import org.junit.Before

import java.awt.image.BufferedImage


class LogAxisRendererTest extends AssertionsForJUnit {

	var vals = List(1.0, math.E, 4.0, 6.0, math.E * math.E, 10000)
	var axis = new LogAxis(vals(0), vals(5))
	
	@Test
	def horizontal = {
		var bI = new BufferedImage(200, 20, BufferedImage.TYPE_INT_RGB)
		var g2 = bI.createGraphics
		axis.renderer.render(axis, g2, new IntRect(0, 0, 200, 20), true)
		g2.dispose
	}
	
	@Test
	def vertical = {
		var bI = new BufferedImage(20, 200, BufferedImage.TYPE_INT_RGB)
		var g2 = bI.createGraphics
		axis.renderer.render(axis, g2, new IntRect(0, 0, 20, 200), false)
		g2.dispose
	}
	
	@Test
	def nonOrigo = {
		var bI = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB)
		var g2 = bI.createGraphics
		axis.renderer.render(axis, g2, new IntRect(10, 10, 20, 180), false)
		g2.dispose
	}
}