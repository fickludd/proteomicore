package se.lth.immun.mzml

import se.lth.immun.xml.XmlWriter

class MzMLDataWriters(
	var specCount:Int,
	var writeSpectra:XmlWriter => Unit,
	var chromCount:Int,
	var writeChromatograms:XmlWriter => Unit
) {}