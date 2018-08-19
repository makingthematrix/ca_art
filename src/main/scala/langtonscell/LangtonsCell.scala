package langtonscell

import engine.{Automaton, AutomatonCell}
import fields._

case class LangtonsCell(color: WhiteBlack,
                        dir: Option[Dir2D],
                        override val pos: Pos2D,
                        private val auto: Automaton[LangtonsCell],
                        override val generation: Int) extends AutomatonCell[LangtonsCell] {

  def updateColor: WhiteBlack = dir match {
    case Some(_) => color.toggle
    case _       => color
  }

  def updateDir: Option[Dir2D] = dir match {
    case None =>
      auto.near(this).find { case (d, c) => c.dir.contains(d.turnAround) } match {
        case Some((d, _)) =>
          Some(color match {
            case White => d.turnLeft
            case Black => d.turnRight
          })
        case _=> None
      }
    case _ => None
  }

  override  def update: LangtonsCell = {
   // println(s"update! $pos: dir = $dir, color = $color")
    val res = copy(color = updateColor, dir = updateDir, generation = generation + 1)
    //if (color != res.color) println(s"($generation) $pos changed from $color to ${res.color}")
    //if (dir != res.dir) println(s"($generation) $pos changed from $dir to ${res.dir}")
    res
  }

  override def hashCode(): Int = {
    val colorHash = color match {
      case White => 1
      case Black => 2
    }

    colorHash * 6 + dir.fold(0)(_.index + 1)
  }

  override def canEqual(that: Any): Boolean = that.isInstanceOf[LangtonsCell]

  override def equals(obj: scala.Any): Boolean = {
    val cell = obj.asInstanceOf[LangtonsCell]
    color == cell.color && dir == cell.dir
  }
}

object LangtonsCell {
  def apply(pos: Pos2D, world: Automaton[LangtonsCell]): LangtonsCell = LangtonsCell(White, None, pos, world, 0)
}

