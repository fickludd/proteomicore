package se.lth.immun.xlink

import se.lth.immun.chem._
import collection.JavaConversions._

object XLink {
	
	trait PeptidePosition {
		def pep:Peptide
	}
	// -1 means n-term, pos == peptide length means c-term
	case class KnownPosition(pos:Int, val pep:Peptide) extends PeptidePosition
	case class UnknownPosition(val pep:Peptide) extends PeptidePosition
	
	trait Position
	case class SinglePosition(pos:Int) extends Position
	case class LoopPositions(pos1:Int, pos2:Int) extends Position
	
	case class Link(pepPos1:PeptidePosition, pepPos2:PeptidePosition)
	
	
	val DSS = new Molecule(Array(Element.C, Element.H, Element.O), Array(8, 10, 2))
	val AMMONIUM = new ElementComposition(Array(Element.N, Element.H), Array(1, 3))
	val WATER = new ElementComposition(Array(Element.O, Element.H), Array(1, 2))
	
	
	def fromString(str:String, pepParser:String => Peptide, kojakConf:KojakXLink.Conf):Either[XLink, String] = {
		IMPXLink.fromString(str, pepParser) match {
			case Left(xl) => Left(xl)
			case Right(impErr) =>
				KojakXLink.fromString(str, kojakConf) match {
					case Left(xl) => Left(xl)
					case Right(kojakErr) =>
						Right("str not parseble as IMP or Kojak XL:\n%s\n%s".format(impErr, kojakErr))
				}
		}
			
	}
	
	
	
}

class XLink(
		val pep1:Peptide,
		val pep2:Option[Peptide],
		val linker:Molecule,
		val positions:Seq[XLink.Link],
		val isotopeLabel:Option[ElementComposition]
) extends IMolecule {

	def getComposition(): se.lth.immun.chem.ElementComposition = {
		var main = 
			positions.map(_ => linker).foldLeft(pep1.getComposition)((ec1, ec2) => ec1.join(ec2.getComposition))
		
		if (pep2.isDefined)
			main = main.join(pep2.get.getComposition)
		
		if (isotopeLabel.isDefined)
			main.join(isotopeLabel.get)
		else
			main
	}
	
	def getIsotopeDistribution(): se.lth.immun.chem.IsotopeDistribution = 
		getComposition.getIsotopeDistribution
	
	def monoisotopicMass(): Double = 
		pep1.monoisotopicMass + linker.monoisotopicMass * positions.length +
		(if (pep2.isDefined) pep2.get.monoisotopicMass else 0) +
		(if (isotopeLabel.isDefined) isotopeLabel.get.monoisotopicMass else 0)
}