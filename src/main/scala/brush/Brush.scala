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

  private[Brush] lazy val dirToCenter = center.map(cPos => (cPos - pos).approx8)

  override def update: Option[Brush] = dirToCenter.fold(Option.empty[Brush]){ cDir =>
    Some(copy(color = newColor, brushes = newBrushes(cDir)))
  }

  private def newColor =
    if (brushes.nonEmpty) CMYK.sum(brushes)
    else if (color.abs < 0.1) CMYK.White
    else color * 0.75


  private def newBrushes(cDir: Dir2D): List[CMYK] = {
    val near = moore(this)
    val dirsToCheck = cDir match {
      case Up => (DownLeft, Down, DownRight)
      case UpRight => (Left, DownLeft, Down)
      case Right => (UpLeft, Left, DownLeft)
      case DownRight => (Left, UpLeft, Up)
      case Down => (DownLeft, Down, DownRight)
      case DownLeft => (DownLeft, Down, DownRight)
      case Left => (DownLeft, Down, DownRight)
      case UpLeft => (DownLeft, Down, DownRight)
        val c1 = near(DownLeft)
        val c2 = near(Down)
        val c3 = near(DownRight)
    }
  }
    Neighborhood.moore(this).toList.flatMap {
      case (thisDir, cell) => cell.dirs.filter(dir => dir._1.approx8 == thisDir.turnAround)
    }.map {
      case (thatDir, c) => (cDir, c) // TODO: calculate the new dir taking into account the max allowed rotation
    }
}

object Brush {
  def apply(pos: Pos2D, findCell: Pos2D => Brush): Brush = Brush(CMYK.White, None, List.empty, pos, findCell)

  def automaton(dim: Int): Automaton[Brush] = new Automaton[Brush](dim, apply, Board.apply)

  val angles = Map(
    Up        -> (Dir2D(-1, -2), Dir2D(1, -2)),
    UpRight   -> (Dir2D(1, -2),  Dir2D(2, -1)),
    Right     -> (Dir2D(2, -1),  Dir2D(2, 1)),
    DownRight -> (Dir2D(2, 1),   Dir2D(1, 2)),
    Down      -> (Dir2D(1, 2),   Dir2D(-1, 2)),
    DownLeft  -> (Dir2D(-1, 2),  Dir2D(-2, 1)),
    Left      -> (Dir2D(-2, 1),  Dir2D(-2, -1)),
    UpLeft    -> (Dir2D(-2, -1), Dir2D(-1, -2))
  )

  def angle(dir: Dir2D): Dir2D = angles.collect {
    case (ang, (leftBoundary, rightBoundary)) if !dir.leftOf(leftBoundary) && dir.leftOf(rightBoundary) => ang
  }.head
}

