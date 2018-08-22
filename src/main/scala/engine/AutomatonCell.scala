package engine

import fields.Pos2D

trait AutomatonCell[CA <: AutomatonCell[CA]] {
  val pos: Pos2D
  val findCell: (Pos2D) => CA
  def update: Option[CA]
}
