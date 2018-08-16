package langtonscell

import engine.{Automaton, AutomatonCell}
import fields._

case class LangtonsCell(color: WhiteBlack,
                        dir: Option[Dir2D],
                        override val pos: Pos2D,
                        private val ca: Automaton[LangtonsCell],
                        private val gen: Int) extends AutomatonCell[LangtonsCell] {

  def updateColor: WhiteBlack = dir match {
    case Some(_) => color.toggle
    case _       => color
  }

  def updateDir: Option[Dir2D] = dir match {
    case Some(antDir) if ca.grid(gen).near(this).exists { case (d, c) => c.dir.contains(d.turnAround) } =>
      Some(color match {
        case White => antDir.turnLeft
        case Black => antDir.turnRight
      })
    case _ => None
  }

  override  def update: LangtonsCell = copy(color = updateColor, dir = updateDir, gen = gen + 1)
}

object LangtonsCell {
  def apply(pos: Pos2D, world: Automaton[LangtonsCell]): LangtonsCell = LangtonsCell(White, None, pos, world, 0)
}

