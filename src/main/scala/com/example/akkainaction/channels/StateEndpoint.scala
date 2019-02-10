package com.example.akkainaction.channels

import akka.actor.Actor
import java.util.Date

case class StateEvent(time: Date, state: String)

case class Connection(time: Date, state: Boolean)

class StateEndpoint extends Actor {
  def receive = {
    case Connection(time, true) =>
      context.system.eventStream.publish(StateEvent(time, "Connected"))

    case Connection(time, false) =>
      context.system.eventStream.publish(StateEvent(time, "Disconnected"))
  }
}

class SystemLog extends Actor {
  def receive = {
    case event: StateEvent =>
  }
}

class SystemMonitor extends Actor {
  def receive = {
    case event: StateEvent =>
  }
}
