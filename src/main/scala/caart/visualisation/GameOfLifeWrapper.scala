package caart.visualisation

import caart.Arguments
import caart.engine.Automaton
import caart.gameoflife.GameOfLife
import javafx.scene.paint.Color

final class GameOfLifeWrapper(override val args: Arguments) extends AutoWrapper[GameOfLife] {
  override val auto: Automaton[GameOfLife] = GameOfLife.automaton(args.dim)

  override protected def toColor(c: GameOfLife): Color = if (c.life) Color.BLACK else Color.WHITE

  onLeftClick.foreach { pos =>
    updateBoard {
      auto.updateOne(pos) { cell => cell.copy(life = !cell.life) }
    }
  }
}
