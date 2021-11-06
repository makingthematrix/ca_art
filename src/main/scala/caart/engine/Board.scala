package caart.engine

import caart.engine.fields.Pos2D
import com.typesafe.scalalogging.LazyLogging

import scala.collection.parallel.CollectionConverters.ImmutableMapIsParallelizable
import scala.collection.parallel.immutable.ParMap

/** A 2D board; both the container for cells and the graph of their spatial relations.
  *
  * @param dim length of the edge
  * @param map a map of identifiers to cells
  * @tparam C the exact case class implementing the cell
  */
class Board[C <: Cell[C]](dim: Int, protected val map: ParMap[Pos2D, C]) extends LazyLogging {
  def findCell(pos: Pos2D): C = map(Board.wrap(pos, dim))

  final def next(events: Map[Pos2D, Iterable[C#CE]]): Board[C] =
    copy { c => c.next(events.getOrElse(c.pos, Nil)) }

  def copy(pos: Pos2D)(updater: C => C): Board[C] = {
    val wrappedPos = Board.wrap(pos, dim)
    new Board(dim, map.updated(wrappedPos, updater(map(wrappedPos))))
  }

  def copy(updater: C => C): Board[C] = new Board(dim, map.map { case (pos, cell) => pos -> updater(cell) })

  final def cells: Vector[C] = map.values.toVector

  final def -(board: Board[C]): Vector[C] =
    map.collect { case (id, cell) if board.map(id) != cell => cell }.toVector
}

object Board {
  private val Empty: Board[_] = new Board(0, ParMap.empty[Pos2D, Nothing])
  def empty[C <: Cell[C]]: Board[C] = Empty.asInstanceOf[Board[C]]

  def wrap(pos: Pos2D, dim: Int): Pos2D =
    if (pos.x >= 0 && pos.x < dim) {
      if (pos.y >= 0 && pos.y < dim) pos
      else pos.copy(y = (pos.y + dim) % dim)
    } else {
      if (pos.y >= 0 && pos.y < dim) pos.copy(x = (pos.x + dim) % dim)
      else pos.copy(x = (pos.x + dim) % dim, y = (pos.y + dim) % dim)
    }

  /** Builds a map of identifiers to the automaton's cells.
    *
    * @param dim length of the board's edge
    * @param findCell a function building the cell
    * @tparam C type of the cell
    * @return a parallel map of identifiers (based on the given cell's position) to the cells
    */
  def buildMap[C <: Cell[C]](dim: Int, findCell: Pos2D => C): ParMap[Pos2D, C] =
    Pos2D(dim).map(pos => pos -> findCell(pos)).toMap.par

  /** Builds the board of the given automaton's cells.
    *
    * @param dim length of the board's edge
    * @param findCell a function building the cell
    * @tparam C type of the cell
    * @return the board
    */
  def apply[C <: Cell[C]](dim: Int, findCell: Pos2D => C): Board[C] = new Board(dim, buildMap(dim, findCell))
}
