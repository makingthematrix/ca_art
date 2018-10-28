package langtonsant

import engine.{Automaton, AutomatonCell, Board, Neighborhood}
import fields._

case class LangtonsAnt(color: Boolean,
                       dir: Option[Dir2D],
                       override val pos: Pos2D,
                       override val findCell: Pos2D => LangtonsAnt
                       )
  extends AutomatonCell[LangtonsAnt] {

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

object LangtonsAnt {
  def apply(pos: Pos2D, findCell: Pos2D => LangtonsAnt): LangtonsAnt = LangtonsAnt(false, None, pos, findCell)

  def automaton(dim: Int): Automaton[LangtonsAnt] = new Automaton[LangtonsAnt](dim, apply, LangtonsBoard.apply)

  import scala.collection.parallel.immutable.ParMap

  private class LangtonsBoard(dim: Int, map: ParMap[Int, LangtonsAnt]) extends Board[LangtonsAnt](dim, map) {
    override def next: LangtonsBoard = {
      val updated = map.valuesIterator
        .filter(_.dir.isDefined)
        .flatMap { c => Seq(c, findCell(c.pos.move(c.dir.get))) }
        .flatMap(_.update)
        .map(c => c.pos -> c).toMap

      val (toUpdate, toStay) = map.partition { case (id, c) => updated.keySet.contains(c.pos) }

      new LangtonsBoard(dim, toStay ++ toUpdate.map { case (id, c) => id -> updated(c.pos) })
    }

    override def copy(pos: Pos2D)(updater: LangtonsAnt => LangtonsAnt): LangtonsBoard = {
      val id = Board.id(pos, dim)
      new LangtonsBoard(dim, map.updated(id, updater(map(id))))
    }
  }

  private object LangtonsBoard {
    def apply(dim: Int, build: Pos2D => LangtonsAnt): LangtonsBoard = new LangtonsBoard(dim, Board.buildMap(dim, build))
  }
}
