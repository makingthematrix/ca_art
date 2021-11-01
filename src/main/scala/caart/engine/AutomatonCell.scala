package caart.engine

import caart.engine.fields.Pos2D

/** The trait which must be implemented by every Cellular Automaton's cell class.
  * Every cell must know its position on the board (`pos`), it must have access
  * to other cells of its type (`findCell`), and it must implement the rules which
  * control the creation of its successor (`update`).
  *
  * @tparam C the lower bound for the cell is this trait itself
  */
trait AutomatonCell[C <: AutomatonCell[C]] { self: C =>
  type GC <: GlobalCell[GC]

  val pos: Pos2D
  val auto: Automaton[C]
  def update: Option[C]

  def needsUpdate: Boolean = true
  final def next: C = if (needsUpdate) update.getOrElse(self) else self
}
