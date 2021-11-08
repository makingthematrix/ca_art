package caart

import caart.engine.fields.Pos2D

package object visualisation {
  sealed trait GameState

  object GameState {
    case object Pause extends GameState
    case object Play extends GameState
  }

  sealed trait UserEventType

  object UserEventType {
    case object LeftClick  extends UserEventType
    case object RightClick extends UserEventType
    case object MoveUp     extends UserEventType
    case object MoveDown   extends UserEventType
    case object MoveLeft   extends UserEventType
    case object MoveRight  extends UserEventType
  }

  final case class UserEvent(pos: Option[Pos2D], eventType: UserEventType)
}
