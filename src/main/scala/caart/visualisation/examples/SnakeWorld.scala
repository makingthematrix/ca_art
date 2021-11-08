package caart.visualisation.examples

import caart.engine.{Automaton, GlobalUpdateStrategy, UpdateStrategy}
import caart.Arguments
import caart.engine.fields.Dir2D
import caart.examples.{Snake, SnakeGlobal}
import caart.visualisation.{UserEvent, World, UserEventType}
import javafx.scene.paint.Color

final class SnakeWorld(override protected val args: Arguments) extends World[Snake, SnakeGlobal] {
  override protected val auto: Automaton[Snake, SnakeGlobal] =
    Snake.automaton(
      dim = args.dim,
      updateStrategy = UpdateStrategy.onlySelf[Snake],
      globalUpdateStrategy = GlobalUpdateStrategy.onlyEvents[Snake, SnakeGlobal]
    )

  override protected def toColor(c: Snake): Color = c.cellType match {
    case Snake.Empty      => Color.WHITE
    case Snake.Treat      => Color.ORANGE
    case Snake.Head(_)    => Color.BROWN
    case Snake.Body(_, _) => Color.BLACK
    case Snake.Tail(_)    => Color.BLACK
  }

  override protected def processUserEvent(event: UserEvent): Unit = {
    import Snake.{TurnLeft, TurnRight}
    import UserEventType.{MoveDown, MoveUp, MoveLeft, MoveRight}
    val headDir = auto.globalCell.headDir
    val turn: Option[Snake.GlobalEvent] = event match {
      case UserEvent(_, MoveUp)    if headDir == Dir2D.Right => Some(TurnLeft)
      case UserEvent(_, MoveUp)    if headDir == Dir2D.Left  => Some(TurnRight)
      case UserEvent(_, MoveDown)  if headDir == Dir2D.Right => Some(TurnRight)
      case UserEvent(_, MoveDown)  if headDir == Dir2D.Left  => Some(TurnLeft)
      case UserEvent(_, MoveLeft)  if headDir == Dir2D.Up    => Some(TurnLeft)
      case UserEvent(_, MoveLeft)  if headDir == Dir2D.Down  => Some(TurnRight)
      case UserEvent(_, MoveRight) if headDir == Dir2D.Up    => Some(TurnRight)
      case UserEvent(_, MoveRight) if headDir == Dir2D.Down  => Some(TurnLeft)
      case _ => None
    }
    turn.foreach(auto.addEvent)
  }
}
