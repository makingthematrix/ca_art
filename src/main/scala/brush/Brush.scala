package brush

import engine.{Automaton, AutomatonCell, Board, Near}
import fields._

case class Brush(color: CMYK,
                 center: Option[Pos2D],
                 dirs: List[(Dir2D, CMYK)],
                 override val pos: Pos2D,
                 override val findCell: Pos2D => Brush
                ) extends AutomatonCell[Brush] {

  private lazy val dirToCenter = center.map(_ - pos)

  override def update: Option[Brush] = dirToCenter.fold(Option.empty[Brush]){ cDir =>
    Some(copy(color = newColor, dirs = newDirs(cDir)))
  }

  private def newColor =
    if (dirs.nonEmpty) CMYK.sum(dirs.map(_._2))
    else if (color.abs < 0.1) CMYK.White
    else color * 0.75


  private def newDirs(cDir: Dir2D) =
    Near.near8(this).toList.flatMap {
      case (thisDir, cell) => cell.dirs.filter(dir => Brush.angle(dir._1) == thisDir.turnAround)
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

