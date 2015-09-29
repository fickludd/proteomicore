package se.lth.immun.xlink

import se.lth.immun.chem._
import collection.JavaConversions._
import scala.util.{Either, Left, Right}
import scala.util.{Try, Success, Failure}

object IMPXLink {

	import XLink._
	
	val XLINK_MAP = Map(
			"DSS" -> DSS.getComposition,
			"DSS-n" -> DSS.getComposition.join(AMMONIUM),
			"DSS-h" -> DSS.getComposition.join(WATER))
	
	def fromString(str:String, pepParser:String => Peptide):Either[XLink, String] = {
		val tildeCount = str.count(_ == '~')
		
		if (tildeCount < 1 || tildeCount > 2)
			return Right("'"+str+"' is not parseable as a monolinked peptide or xlinked peptide pair")
		
		val parts = str.split('~')
		
		val linker = new Molecule(Modifier.parseMolecule(parts(1), XLINK_MAP).getComposition)
		
		pepPos(parts.head, pepParser) match {
			case Right(err) => Right(err)
			case Left((pep1, pos1)) =>
				if (parts.length > 2) {
					pepPos(parts.last, pepParser) match {
						case Right(err) => Right(err)
						case Left((pep2, pos2)) =>
							val linkPositions = matchPositions(pos1, pep1, pos2, pep2)
							Left(new XLink(pep1, Some(pep2), linker, linkPositions, None))
					}
				} else {
					val linkPositions = matchPositions(pos1, pep1, Nil, null)
					Left(new XLink(pep1, None, linker, linkPositions, None))
				}
		}
	}
	
	val pepPosRegex = """([^\[]+)\[([,:\d]*)\]""".r.unanchored
	def pepPos(str:String, pepParser:String => Peptide):Either[(Peptide, Seq[Position]), String] =
		str match {
			case pepPosRegex(pepStr, positions) => 
				val pepTry = Try(pepParser(pepStr))
				val posTry:Try[Seq[Position]] = Try(positions.split(",").map(posStr => {
					val parts = posStr.split(":")
					val position = parts.length match {
						case 1 =>
							SinglePosition(parts.head.toInt)
						case 2 =>
							LoopPositions(parts.head.toInt, parts.last.toInt)
						case _ =>
							throw new Exception("Unparsable XL positions '%s'".format(posStr))
					}
					position
				}))
				pepTry match {
					case Failure(e) =>
						Right(e.getMessage)
					case Success(pep) =>
						posTry match {
							case Failure(e) =>
								Right(e.getMessage)
							case Success(pos) =>
								Left((pep, pos))
						}
				}
				
			case _ =>
					Right("not valid peptide position string '%s'".format(str))
		}
	
	
	def matchPositions(
			pos1:Seq[Position], 
			pep1:Peptide, 
			pos2:Seq[Position],
			pep2:Peptide
	):Seq[Link] = {
		val loop1 = pos1.collect {
			case LoopPositions(p1, p2) =>
				Link(KnownPosition(p1, pep1), KnownPosition(p2, pep1))
		}
		val loop2 = pos2.collect {
			case LoopPositions(p1, p2) =>
				Link(KnownPosition(p1, pep1), KnownPosition(p2, pep1))
		}
		val single1 = pos1.collect { case SinglePosition(p) => p }
		val single2 = pos2.collect { case SinglePosition(p) => p }
		
		if (single1.length != single2.length)
			throw new Exception("Single positions don't match up")
		
		val crosses = single1.zip(single2).map(t => Link(KnownPosition(t._1, pep1), KnownPosition(t._2, pep2)))
		
		crosses ++ loop1 ++ loop2
	}
	
	
}