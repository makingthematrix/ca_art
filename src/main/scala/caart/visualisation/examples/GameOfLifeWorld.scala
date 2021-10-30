package caart.visualisation.examples

import caart.Arguments
import caart.engine.{Automaton, Board}
import caart.examples.GameOfLife
import caart.visualisation.{World, UserEvent}
import javafx.scene.paint.Color

final class GameOfLifeWorld(override val args: Arguments) extends World[GameOfLife] {
  override val auto: Automaton[GameOfLife] = GameOfLife.automaton(args.dim)

  override protected def toColor(c: GameOfLife): Color = if (c.life) Color.BLACK else Color.WHITE

  override protected def updateFromEvent(event: UserEvent): Board[GameOfLife] =
    auto.updateOne(event.pos) { cell => cell.copy(life = !cell.life) }
}