package engine

import fields.Pos2D

trait AutomatonCell[CA <: AutomatonCell[CA]] {
  def update: CA
  val pos: Pos2D
  val neighbor: (Pos2D) => CA
}
