package caart.visualisation

import caart.Arguments
import caart.engine.{Automaton, Board}
import caart.gameoflife.GameOfLife
import javafx.scene.paint.Color

final class GameOfLifeWrapper(override val args: Arguments) extends AutoWrapper[GameOfLife] {
  override val auto: Automaton[GameOfLife] = GameOfLife.automaton(args.dim)

  override protected def toColor(c: GameOfLife): Color = if (c.life) Color.BLACK else Color.WHITE

  override protected def updateFromEvent(event: UserEvent): Board[GameOfLife] =
    auto.updateOne(event.pos) { cell => cell.copy(life = !cell.life) }
}
