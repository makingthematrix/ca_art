package caart.chase

import caart.engine.{Automaton, AutomatonCell, AutomatonCreator, Board, Neighborhood}
import caart.fields._
import Neighborhood.moore

final case class Chase(color:   CMYK,
                       center:  Option[Pos2D],
                       brushes: List[CMYK],
                       override val pos: Pos2D,
                       override val findCell: Pos2D => Chase) extends AutomatonCell[Chase] {
  private[Chase] lazy val dirToCenter = center match {
    case None                      => None
    case Some(cPos) if cPos == pos => None
    case Some(cPos)                => Some((cPos - pos).approx8)
  }

  override def update: Option[Chase] = (color, dirToCenter) match {
    case (CMYK.White, None) => None
    case (_, None)          => Some(copy(color = newColor))
    case (_, Some(_))       => Some(copy(color = newColor, brushes = newBrushes))
  }

  private def newColor =
    if (brushes.nonEmpty) CMYK.sum(color :: brushes)
    else if (color.abs < 0.02) CMYK.White
    else color * 0.98

  private def newBrushes: List[CMYK] = moore(this).collect {
    case (thisDir, cell) if cell.brushes.nonEmpty && cell.dirToCenter.contains(thisDir.turnAround) => cell.brushes
  }.flatten.toList

  override def toString: String = s"Chase($pos, color = $color, center = $center, dir to center = $dirToCenter, brushes = $brushes)"
}

object Chase extends AutomatonCreator[Chase] {
  def apply(pos: Pos2D, findCell: Pos2D => Chase): Chase = Chase(CMYK.White, None, List.empty, pos, findCell)
  def automaton(dim: Int): Automaton[Chase] = new Automaton[Chase](dim, apply, Board.apply)
}

