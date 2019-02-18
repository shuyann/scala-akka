package com.example.official.actors

import scala.concurrent.duration._

import akka.actor._

object TimerActor {

  private case object TickKey

  private case object FirstTick

  private case object Tick

}

class TimerActor extends Actor with ActorLogging with Timers {

  import TimerActor._

  timers.startSingleTimer(TickKey, FirstTick, 500 millis)

  def receive = {
    case FirstTick =>
      // do something useful here
      timers.startPeriodicTimer(TickKey, Tick, 1 second)
    case Tick =>
    // do something useful here
  }
}

class TimerScheduledMessages extends App {

  val system = ActorSystem("timerSystem")

  val timerActor = system.actorOf(Props[TimerActor])

  system.terminate()
}
