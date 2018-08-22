package langtonscell

import engine.Board
import engine.Board.buildMap
import engine.Near.near4
import fields.Pos2D

import scala.collection.parallel.immutable.ParMap

class LangtonsBoard(dim: Int, map: ParMap[Int, LangtonsCell]) extends Board[LangtonsCell](dim, map) {
  override def next: LangtonsBoard = {
    val updated = map.valuesIterator
      .filter { _.dir.isDefined }
      .flatMap(c => c :: near4(c, findCell).values.toList)
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
