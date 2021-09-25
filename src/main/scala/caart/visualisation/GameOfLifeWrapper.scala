package caart.visualisation

import caart.Arguments
import caart.engine.Automaton
import caart.fields.{Pos2D, RGB}
import caart.gameoflife.GameOfLife
import com.wire.signals.ui.UiDispatchQueue.Ui
import javafx.scene.paint.Color

import scala.concurrent.Future

class GameOfLifeWrapper(override val args: Arguments) extends AutoWrapper[GameOfLife] {
  override val auto: Automaton[GameOfLife] = GameOfLife.automaton(args.dim)

  override protected def toColor(c: GameOfLife): Color = {
    val rgb = if (c.life) RGB.Black else RGB.White
    Color.rgb(rgb.r, rgb.g, rgb.b)
  }

  override def updateOne(pos: Pos2D): Unit = {
    auto.updateOne(pos) { cell => cell.copy(life = !cell.life) }
    Future { tileMap(pos).refresh() }(Ui)
  }
}
