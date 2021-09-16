package caart.engine

import caart.fields.Pos2D
import scala.collection.parallel.immutable.ParMap

/** A 2D board; both the container for cells and the graph of their spatial relations.
  *
  * @param dim length of the edge
  * @param map a map of identifiers to cells
  * @tparam C the exact case class implementing the cell
  */
class Board[C <: AutomatonCell[C]](dim: Int, protected val map: ParMap[Int, C]) {
  def findCell(pos: Pos2D): C = map(Board.id(pos, dim))

  def next: Board[C] = new Board(dim, map.map { case (k, c) => k -> c.update.getOrElse(c) })

  def copy(pos: Pos2D)(updater: C => C): Board[C] = {
    val id = Board.id(pos, dim)
    new Board(dim, map.updated(id, updater(map(id))))
  }

  def copy(updater: C => C): Board[C] = new Board(dim, map.map { case (id, cell) => (id, updater(cell)) })

  def values: List[C] = map.values.toList

  def -(board: Board[C]): List[C] = Pos2D(dim).flatMap { p =>
    val c = findCell(p)
    if (c != board.findCell(p)) Some(c) else None
  }
}

object Board {
  /** A method converting the given position to the identifier of a cell at that position.
    *
    * If the given position is outside the board, the method uses modulo(dim) to wrap it around.
    * Essentially that means that the board is the surface of a 3D torus, with no edges in either direction.
    *
    * @param pos position on a 2D plane; if it's bigger than `dim` it will be wrapped around
    * @param dim length of the edge
    * @return the identifier
    */
  def id(pos: Pos2D, dim: Int): Int = {
    def wrap(i: Int) = i % dim match {
      case x if x >= 0 => x
      case x           => x + dim
    }

    wrap(pos.x) * dim + wrap(pos.y)
  }

  /** Builds a map of identifiers to the automaton's cells.
    *
    * @param dim length of the board's edge
    * @param applyCell a function building the cell
    * @tparam C type of the cell
    * @return a parallel map of identifiers (based on the given cell's position) to the cells
    */
  def buildMap[C <: AutomatonCell[C]](dim: Int, applyCell: Pos2D => C): ParMap[Int, C] =
    Pos2D(dim).foldLeft(ParMap.empty[Int, C])((map, pos) => map + (id(pos, dim) -> applyCell(pos)))

  /** Builds the board of the given automaton's cells.
    *
    * @param dim length of the board's edge
    * @param applyCell a function building the cell
    * @tparam C type of the cell
    * @return the board
    */
  def apply[C <: AutomatonCell[C]](dim: Int, applyCell: Pos2D => C): Board[C] = new Board(dim, buildMap(dim, applyCell))
}
