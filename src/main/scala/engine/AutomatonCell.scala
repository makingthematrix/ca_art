package engine

import fields.Pos2D

trait AutomatonCell[CA <: AutomatonCell[CA]] {
  def update: CA
  def pos: Pos2D
}
