package caart.visualisation.examples

import caart.Arguments
import caart.engine.Automaton
import caart.engine.GlobalCell.Empty
import caart.examples.GameOfLife
import caart.visualisation.{UserEvent, World}
import javafx.scene.paint.Color

final class GameOfLifeWorld(override val args: Arguments) extends World[GameOfLife, Empty[GameOfLife]] {
  override val auto: Automaton[GameOfLife, Empty[GameOfLife]] = GameOfLife.automaton(args.dim)

  override protected def toColor(cell: GameOfLife): Color =
    auto.updatedByEvents(cell) match {
      case Some(newCell) if newCell.life => Color.RED
      case None if cell.life => Color.BLACK
      case _ => Color.WHITE
    }

  override protected def processUserEvent(event: UserEvent): Unit =
    auto.addEvent(event.pos, GameOfLife.FlipCell)
}