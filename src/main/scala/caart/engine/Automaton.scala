package caart.engine

import caart.engine.fields.{Dir2D, Pos2D}

/** The main class of a cellular automaton.
  *
  * To create a cellular automaton, the user has to specify the size of the board, `dim`, (for simpilicity reasons 
  * before the ScalaIO talk I assumed the board has the same length in all dimensions), and provide 
  * a function which will be used to create automaton's cells. Optionally, the user can provide a specialized
  * function for creating the board, eg. in case of c.a. which can be optimized by skipping computations
  * on a part of the board. 
  *
  * TODO: Move away from the default board being a torus with a square surface. `dim` should be specified inside 
  * `applyBoard`, and `applyBoard` should not be optional. Instead, allow for choosing from  a list of utility methods, 
  * similar to how neighborhoods work.
  *
  * @constructor Takes the board edge size, a function for creating cells, and a function to create the board.
  */
class Automaton[C <: AutomatonCell[C]](dim: Int,
                                       private val createCell:  (Pos2D, Automaton[C]) => C,
                                       private val createBoard: (Int, Pos2D => C) => Board[C])
  extends Iterator[Board[C]] {
  private var board: Board[C] = createBoard(dim, createCell(_, this))

  override def next(): Board[C] = {
    board = board.next
    board
  }

  override def hasNext: Boolean = true

  def current: Board[C] = board

  /** Updates the current state of the iterator.
    *
    * Used to provide the initial state of the automaton or to perform a change in the middle of the main loop.
    * `updater` is a function which for a given cell returns its updated version (or the cell itself, without
    * changes). It's called for every cell on the board in order to create an updated copy of the board, which
    * in turn replaces the old one.
    *
    * @param updater Called for every cell; creates an updated copy or returns the old one.
    * @return the updated board
    */
  def update(updater: C => C): Board[C] = {
    board = board.copy(updater)
    board
  }

  def updateOne(pos: Pos2D)(updater: C => C): Board[C] = {
    board = board.copy(pos)(updater)
    board
  }

  def cells: Vector[C] = board.cells
  val positions: Set[Pos2D] = Pos2D(dim).toSet
  def findCell(pos: Pos2D): C = board.findCell(pos)


  /**
    * The von Neumann's neighborhood is a collection of four cells which are
    * up, right, down, and left from the given one. The method returns them
    * as a map where keys are the corresponding Dir2D constants.
    */
  def neumann(pos: Pos2D): Map[Dir2D, C] =
    Dir2D.dirs4.map(dir => dir -> board.findCell(pos.move(dir))).toMap

  /**
    * The Moore's neighborhood is a collection of eight cells which are
    * up, up-right, right, right-down, down, down-left, left, and left-up from
    * the given one. The method returns them as a map where keys are
    * the corresponding Dir2D constants.
    */
  def moore(pos: Pos2D): Map[Dir2D, C] =
    Dir2D.dirs8.map(dir => dir -> board.findCell(pos.move(dir))).toMap
}

trait AutomatonCreator[C <: AutomatonCell[C]] {
  def cell(pos: Pos2D, auto: Automaton[C]): C
  def automaton(dim: Int): Automaton[C] = new Automaton[C](dim, cell, Board.apply)
}
