package caart.engine

import caart.fields.Dir2D

/**
  * Utility methods for collecting the neighborhood of a given cell.
  * The choice of the method belongs to the cell asking for the neighborhood.
  */
object Neighborhood {
/**
  * The von Neumann's neighborhood is a collection of four cells which are
  * up, right, down, and left from the given one. The method returns them
  * as a map where keys are the corresponding Dir2D constants.
  */
  def neumann[C <: AutomatonCell[C]](cell: C): Map[Dir2D, C] =
    Dir2D.dirs4.map(dir => dir -> cell.findCell(cell.pos.move(dir))).toMap

/**
  * The Moore's neighborhood is a collection of eight cells which are
  * up, up-right, right, right-down, down, down-left, left, and left-up from 
  * the given one. The method returns them as a map where keys are 
  * the corresponding Dir2D constants.
  */
  def moore[C <: AutomatonCell[C]](cell: C): Map[Dir2D, C] =
    Dir2D.dirs8.map(dir => dir -> cell.findCell(cell.pos.move(dir))).toMap
}
