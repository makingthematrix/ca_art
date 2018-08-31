package engine

import fields.{Dir2D, Pos2D}

object Near {
  def near4[CA <: AutomatonCell[CA]](ca: CA): Map[Dir2D, CA] =
    Dir2D.dirs4.map(dir => dir -> ca.findCell(ca.pos.move(dir))).toMap

  def near8[CA <: AutomatonCell[CA]](ca: CA): Map[Dir2D, CA] =
    Dir2D.dirs8.map(dir => dir -> ca.findCell(ca.pos.move(dir))).toMap
}
