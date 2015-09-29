package se.lth.immun.xlink

import scala.collection.mutable.ArrayBuffer
import se.lth.immun.chem._
import scala.util.{Either, Left, Right}

object KojakXLink {
	
	import XLink._
	
	case class Conf(
			val linker:Molecule,
			val fixedMods:Map[StandardAminoAcid, Molecule])
	
	val CARBAMIDOMETHYL = new Molecule(new ElementComposition(
			Array(Element.H, Element.C, Element.O, Element.N),
			Array(3, 2, 1, 1)
		))
	val DSS_CARB_CONF = KojakXLink.Conf(XLink.DSS, Map(StandardAminoAcid.C -> CARBAMIDOMETHYL))
	
	val KNOWN_MODS = Map(
				155.09 -> new Molecule(XLink.DSS.getComposition().join(XLink.AMMONIUM)),
				156.08 -> new Molecule(XLink.DSS.getComposition().join(XLink.WATER)),
				15.99 -> new Molecule(new ElementComposition(Array(Element.O), Array(1)))
			)
	
	
	def fromString(str:String, conf:Conf):Either[XLink, String] = {
		val barCount = str.count(_ == '-')
		val isLoop = str.contains("-LOOP")
		val isCross = str.contains("--")
		val stripped = str.trim.drop(2).dropRight(2)
		
		if (isLoop && isCross)
			return Right("Kojak crosslink cannot be both cross and loop")
		
		if (isLoop) {
			val (pepRes, positions) = pepPos(stripped.dropRight(5), conf)
			if (positions.length != 2)
				return Right("Need exactly 2 positions for Loop crosslinks, got '%d': %s".format(positions.length, stripped))
			
			pepRes match {
				case Left(pep) =>
					Left(new XLink(
						pep, 
						None, 
						conf.linker, 
						List(Link(
							KnownPosition(positions.head, pep), 
							KnownPosition(positions.last, pep)
						)), 
						None
					))
				
				case Right(err) => Right(err)
			}
				
			
		} else if (isCross) {
			val parts = stripped.split("--")
			if (parts.length != 2)
				return Right("Need exactly 2 peptides for crossed crosslinker, got %d: %s".format(parts.length, stripped))
			
			val (pepRes1, pos1) = pepPos(parts.head, conf)
			val (pepRes2, pos2) = pepPos(parts.last, conf)
			
			if (pos1.length != 1 || pos2.length != 1)
				return Right("Need exactly 1 position per peptide for crossed crosslinker, got %d and %d: %s".format(pos1.length, pos2.length, stripped))
			
			pepRes1 match {
				case Right(err) => Right(err)
				case Left(pep1) =>
					pepRes2 match {
						case Right(err) => Right(err)
						case Left(pep2) =>
							Left(new XLink(
								pep1,
								Some(pep2),
								conf.linker,
								List(Link(
									KnownPosition(pos1.head, pep1),
									KnownPosition(pos2.head, pep2)
								)),
								None
							))
					}
			}
				
			
		} else {
			kojakPepFromSequence(stripped, conf) match {
				case Right(err) => Right(err)
				case Left(pep) =>
					Left(new XLink(pep, None, conf.linker, List(), None))
			}
		}
	}
	
	val pepPosRegex = """([^\(]+)\(([,\d]+)\)""".r.unanchored
	def pepPos(str:String, conf:Conf) =
		str match {
			case pepPosRegex(pepStr, positions) => 
				val pep = kojakPepFromSequence(pepStr, conf)
				(pep, positions.split(",").map(_.toInt))
		}
	
	def kojakPepFromSequence(seq:String, conf:Conf):Either[Peptide, String] = {
		val aas = new ArrayBuffer[IAminoAcid]
		var is = 0
		var sl = seq.length
		while (is < sl) {
			val saa = StandardAminoAcid.fromChar(seq.charAt(is))
			if (saa == null)
				return Right("Cannot parse amino acid '"+seq.charAt(is)+"' in peptide '"+seq+"'")
			
			val aa = 
				conf.fixedMods.get(saa).map(mod => 
					new ModifiedAminoAcid(saa, mod)
				).getOrElse(saa)
				
			is += 1
			if (is < sl && seq.charAt(is) == '[') {
				is += 1
				val start = is
				while (seq.charAt(is) != ']' && is < sl) is += 1
				if (is == sl)
					return Right("Cannot parse kojak modification '"+seq.substring(start-1)+"' in peptide '"+seq+"'");
				
				val mass = seq.substring(start, is).toDouble
				KNOWN_MODS.get(mass) match {
					case None =>
						return Right("Unknown kojak modification with mass '"+mass+"' in peptide '"+seq+"'")
				
					case Some(molecule) =>
						aas += new ModifiedAminoAcid(aa, molecule)
				}
				
				is += 1
				
			} else 
				aas += aa
		}
		
		Left(new Peptide(aas.toArray))
	}
}