package engine

import fields.{Dir2D, Pos2D}

object Near {
  def near4[CA <: AutomatonCell[CA]](ca: CA, neighbor: (Pos2D) => CA): Map[Dir2D, CA] =
      Dir2D.dirs.map(dir => dir -> neighbor(ca.pos.move(dir))).toMap
}
