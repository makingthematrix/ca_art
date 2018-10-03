package engine

import fields.Pos2D

trait AutomatonCell[C <: AutomatonCell[C]] {
  val pos: Pos2D
  val findCell: (Pos2D) => C
  def update: Option[C]
}
