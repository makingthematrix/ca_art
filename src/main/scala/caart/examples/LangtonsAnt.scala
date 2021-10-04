package caart.examples

import caart.engine.{AutomatonCell, AutomatonCreator, Neighborhood}
import caart.engine.fields.{Dir2D, Pos2D}

final case class LangtonsAnt(override val pos: Pos2D,
                             override val findCell: Pos2D => LangtonsAnt,
                             color: Boolean = false,
                             dir: Option[Dir2D] = None) extends AutomatonCell[LangtonsAnt] {
  /* In case of Langton's Ant in every iteration only a small part of the board is updated
   * ( `2*n / (dim*dim)` where `n` is the number of ants on the board). We can speed it up
   * by overriding `needsUpdate` with a quick check if the update is needed at all.
   */
  override def needsUpdate: Boolean =
    dir.isDefined || Neighborhood.neumann(this).exists(p => p._2.dir.isDefined)

  override  def update: Option[LangtonsAnt] = (newColor, newDir) match {
    case (c, d) if c == color && d == dir => None
    case (c, d)                           => Some(copy(color = c, dir = d))
  }

  private def newColor = if (dir.isEmpty) color else !color

  private def newDir = Neighborhood.neumann(this).find {
    case (thisDir, cell) => cell.dir.contains(thisDir.turnAround)
  }.map {
    case (thisDir, _) if color => thisDir.turnLeft
    case (thisDir, _)          => thisDir.turnRight
  }
}

object LangtonsAnt extends AutomatonCreator[LangtonsAnt] {
  def cell(pos: Pos2D, findCell: Pos2D => LangtonsAnt): LangtonsAnt = LangtonsAnt(pos, findCell)
}
