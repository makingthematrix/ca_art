package engine

import fields.Pos2D

/**
* The trait which must be implemented by every Cellular Automaton's cell class.
* Every cell must know its position on the board (`pos`), it must have access
* to other cells of its type (`findCell`), and it must implement the rules which 
* control the creation of its successor (`update`).
*/
trait AutomatonCell[C <: AutomatonCell[C]] {
  val pos: Pos2D
  val findCell: (Pos2D) => C
  def update: Option[C]
}
