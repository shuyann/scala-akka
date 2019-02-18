package com.example.official.scheduler

import akka.actor._
import scala.concurrent.duration._

case object Tick

class TickActor extends Actor {
  def receive = {
    case Tick => // do something
  }
}

object Examples extends App {
  val system = ActorSystem("schedulerSystem")
  val tickActor = system.actorOf(Props[TickActor])

  import system.dispatcher

  val cancellable =
    system.scheduler.schedule(
      0 milliseconds,
      50 milliseconds,
      tickActor,
      Tick)
  cancellable.cancel()

  system.terminate()

}
