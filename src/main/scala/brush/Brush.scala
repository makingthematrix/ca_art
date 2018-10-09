package brush

import engine.{Automaton, AutomatonCell, Board, Neighborhood}
import fields._
import Neighborhood.moore

case class Brush(color: CMYK,
                 center: Option[Pos2D],
                 brushes: List[CMYK],
                 override val pos: Pos2D,
                 override val findCell: Pos2D => Brush
                ) extends AutomatonCell[Brush] {

  private[Brush] lazy val dirToCenter = center match {
    case None                      => None
    case Some(cPos) if cPos == pos => None
    case Some(cPos)                => Some((cPos - pos).approx8)
  }

  override def update: Option[Brush] = (color, dirToCenter) match {
    case (CMYK.White, None) => None
    case (_, None)          => Some(copy(color = newColor))
    case (_, Some(cDir))    => Some(copy(color = newColor, brushes = newBrushes(cDir)))
  }

  private def newColor =
    if (brushes.nonEmpty) CMYK.sum(color :: brushes)
    else if (color.abs < 0.1) CMYK.White
    else color * 0.75

  private def newBrushes(cDir: Dir2D): List[CMYK] = {
    moore(this).collect {
      case (thisDir, cell) if cell.brushes.nonEmpty && cell.dirToCenter.contains(thisDir.turnAround) => cell.brushes
    }.flatten.toList
  }

  override def toString: String = s"Brush($pos, color = $color, center = $center, dir to center = $dirToCenter, brushes = $brushes)"
}

object Brush {
  def apply(pos: Pos2D, findCell: Pos2D => Brush): Brush = Brush(CMYK.White, None, List.empty, pos, findCell)

  def automaton(dim: Int): Automaton[Brush] = new Automaton[Brush](dim, apply, Board.apply)
}

