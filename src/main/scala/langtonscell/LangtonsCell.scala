package langtonscell

import engine.Board.buildMap
import engine.{Automaton, AutomatonCell, Board}
import engine.Near.near4
import fields._

import scala.collection.parallel.immutable.ParMap

case class LangtonsCell(color: Boolean,
                        dir: Option[Dir2D],
                        override val pos: Pos2D,
                        override val findCell: Pos2D => LangtonsCell
                       )
  extends AutomatonCell[LangtonsCell] {

  override  def update: Option[LangtonsCell] = {
    val near = near4(this, findCell)
    if (dir.isEmpty && near.forall(_._2.dir.isEmpty)) None
    else (newColor, newDir(near)) match {
      case (c, d) if c == color && d == dir => None
      case (c, d)                           => Some(copy(color = c, dir = d))
    }
  }

  private def newColor = if (dir.isEmpty) color else !color

  private def newDir(near: Map[Dir2D, LangtonsCell]) =
    near.find {
      case (thisDir, cell) => cell.dir.contains(thisDir.turnAround)
    }.map {
      case (thisDir, _) => if (color) thisDir.turnLeft else thisDir.turnRight
    }
}

class LangtonsBoard(dim: Int, map: ParMap[Int, LangtonsCell]) extends Board[LangtonsCell](dim, map) {
  override def next: LangtonsBoard = {
    val updated = map.valuesIterator
      .filter(_.dir.isDefined)
      .flatMap { c => Seq(c, findCell(c.pos.move(c.dir.get))) }
      .flatMap(_.update)
      .map(c => c.pos -> c).toMap

    val (toUpdate, toStay) = map.partition { case (id, c) => updated.keySet.contains(c.pos) }

    new LangtonsBoard(dim, toStay ++ toUpdate.map { case (id, c) => id -> updated(c.pos) })
  }

  override def copy(pos: Pos2D)(updater: LangtonsCell => LangtonsCell): LangtonsBoard = {
    val id = Board.id(pos, dim)
    new LangtonsBoard(dim, map.updated(id, updater(map(id))))
  }
}

object LangtonsBoard {
  def apply(dim: Int, build: Pos2D => LangtonsCell): LangtonsBoard = new LangtonsBoard(dim, buildMap(dim, build))
}

object LangtonsCell {
  def apply(pos: Pos2D, findCell: Pos2D => LangtonsCell): LangtonsCell = LangtonsCell(false, None, pos, findCell)

  def automaton(dim: Int)(init: Board[LangtonsCell] => Board[LangtonsCell]): Automaton[LangtonsCell] =
    new Automaton[LangtonsCell](dim, init, LangtonsBoard.apply, apply)
}
