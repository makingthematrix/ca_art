package langtonscell

import engine.Board
import engine.Board.buildMap
import engine.Near.near4
import fields.Pos2D

import scala.collection.parallel.immutable.ParMap


class LangtonsBoard(dim: Int, map: ParMap[Int, LangtonsCell]) extends Board[LangtonsCell](dim, map) {
  override def newBoard(map: ParMap[Int, LangtonsCell]): Board[LangtonsCell] = new LangtonsBoard(dim, map)

  override def next: Board[LangtonsCell] = {
    val updated =
      map.valuesIterator.filter { _.dir.isDefined }
        .flatMap(c => c :: near4(c, findCell).values.toList)
        .flatMap(_.update)
        .map(c => c.pos -> c).toMap

    val (toUpdate, toStay) = map.partition { case (id, c) => updated.keySet.contains(c.pos) }

    newBoard(toStay ++ toUpdate.map { case (id, c) => id -> updated(c.pos) })
  }
}

object LangtonsBoard {
  def apply(dim:Int, build: Pos2D => LangtonsCell): LangtonsBoard = new LangtonsBoard(dim, buildMap(dim, build))
}
