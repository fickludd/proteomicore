package se.lth.immun.mzml

import se.lth.immun.xml.XmlWriter

class MzMLDataWriters(
	var specCount:Int,
	var writeSpectra:XmlWriter => Seq[MzML.OffsetRef],
	var chromCount:Int,
	var writeChromatograms:XmlWriter => Seq[MzML.OffsetRef]
) {}