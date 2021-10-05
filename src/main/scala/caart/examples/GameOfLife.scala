package caart.examples

import caart.engine.{AutomatonCell, AutomatonCreator, Neighborhood}
import caart.engine.fields.Pos2D

final case class GameOfLife(override val pos: Pos2D,
                            override val findCell: Pos2D => GameOfLife,
                            life: Boolean = false) extends AutomatonCell[GameOfLife] {
  override def update: Option[GameOfLife] =
    Neighborhood
      .moore(this)
      .count { case (_, c) => c.life } match {
        case 3 if !life                    => Some(copy(life = true))
        case n if life && (n < 2 || n > 3) => Some(copy(life = false))
        case _                             => None
      }
}

object GameOfLife extends AutomatonCreator[GameOfLife] {
  def cell(pos: Pos2D, findCell: Pos2D => GameOfLife): GameOfLife = GameOfLife(pos, findCell)
}
