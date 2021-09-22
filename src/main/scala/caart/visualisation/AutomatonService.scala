package caart.visualisation

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.dsl.FXGL
import javafx.event.{Event, EventType}

class NextEvent(eventType: EventType[_ <: Event]) extends Event(eventType)

object NextEvent {
  val NextEventType = new EventType(Event.ANY, "NEXT_EVENT")
}

class AutomatonService extends EngineService {
  private lazy val eventListener = FXGL.getEventBus.addEventHandler(NextEvent.NextEventType, { _ : Event =>
    FXGL.getAppCast[FXGLApp].next()
  })

  override def onInit(): Unit = {
    eventListener
  }
}
