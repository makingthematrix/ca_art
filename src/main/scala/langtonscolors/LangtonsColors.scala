package langtonscolors

import engine.{Automaton, AutomatonCell, AutomatonCreator, Board}
import engine.Neighborhood.neumann
import fields.{CMYK, Dir2D, Pos2D}

final case class LangtonsColors(colors: Set[CMYK],
                                dirs: List[(Dir2D, CMYK)],
                                override val pos: Pos2D,
                                override val findCell: Pos2D => LangtonsColors) extends AutomatonCell[LangtonsColors] {
  override  def update: Option[LangtonsColors] = (newColors, newDirs) match {
    case (cs, ds) if cs == colors && ds == dirs => None
    case (cs, ds)                               => Some(copy(colors = cs, dirs = ds))
  }

  private def newColors = {
    val newColors = dirs.map(_._2).toSet
    (colors | newColors) &~ (colors & newColors) // no generic xor?
  }

  private def newDirs = neumann(this).toList.flatMap {
    case (thisDir, cell) => cell.dirs.filter(_._1 == thisDir.turnAround)
  }.map {
    case (thatDir, color) if colors.contains(color) => (thatDir.turnRight, color)
    case (thatDir, color) =>                           (thatDir.turnLeft, color)
  }
}

object LangtonsColors extends AutomatonCreator[LangtonsColors] {
  def apply(pos: Pos2D, findCell: Pos2D => LangtonsColors): LangtonsColors = LangtonsColors(Set.empty, List.empty, pos, findCell)
  def automaton(dim: Int): Automaton[LangtonsColors] = new Automaton[LangtonsColors](dim, apply, LColorsBoard.apply)

  import scala.collection.parallel.immutable.ParMap

  /** A board optimized for Langton's Ant
    *
    * In the case of Langton's Ant in every iteration only a small part of the board is updated
    * ( `2*n / (dim*dim)` where `n` is the number of ants on the board). It is possible to check
    * quickly and filter out all the other cells, and perform full computations only for the
    * affected ones.
    *
    * @param dim length of the edge
    * @param map a map of identifiers to cells
    */
  private class LColorsBoard(dim: Int, map: ParMap[Int, LangtonsColors]) extends Board[LangtonsColors](dim, map) {
    override def next: LColorsBoard = {
      val updated = map.valuesIterator
        .filter(_.dirs.nonEmpty)
        .flatMap { c => c :: c.dirs.map { case (d, _) => findCell(c.pos.move(d)) } }
        .flatMap(_.update)
        .map(c => c.pos -> c).toMap

      val (toUpdate, toStay) = map.partition { case (_, c) => updated.keySet.contains(c.pos) }

      new LColorsBoard(dim, toStay ++ toUpdate.map { case (id, c) => id -> updated(c.pos) })
    }

    override def copy(pos: Pos2D)(updater: LangtonsColors => LangtonsColors): LColorsBoard = {
      val id = Board.id(pos, dim)
      new LColorsBoard(dim, map.updated(id, updater(map(id))))
    }
  }

  private object LColorsBoard {
    def apply(dim: Int, build: Pos2D => LangtonsColors): LColorsBoard = new LColorsBoard(dim, Board.buildMap[LangtonsColors](dim, build))
  }
}
