package engine

import fields.Dir2D

object Neighborhood {
  def neumann[C <: AutomatonCell[C]](cell: C): Map[Dir2D, C] =
    Dir2D.dirs4.map(dir => dir -> cell.findCell(cell.pos.move(dir))).toMap

  def moore[C <: AutomatonCell[C]](cell: C): Map[Dir2D, C] =
    Dir2D.dirs8.map(dir => dir -> cell.findCell(cell.pos.move(dir))).toMap
}
