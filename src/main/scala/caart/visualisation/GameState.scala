package caart.visualisation

sealed trait GameState

object GameState {
  case object Pause extends GameState
  case object Play extends GameState
}

