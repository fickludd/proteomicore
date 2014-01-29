package se.lth.immun.mzml

class MzMLDataHandlers(
	var specCount:Int => Unit,
	var spectrum:Spectrum => Unit,
	var chromCount:Int => Unit,
	var chromatogram:Chromatogram => Unit
) {}