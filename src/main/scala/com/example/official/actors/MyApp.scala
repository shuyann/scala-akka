package com.example.official.actors

import akka.actor._
import language.postfixOps
import scala.concurrent.duration._

class MyActor extends Actor with ActorLogging {

  def receive = {
    case "test" => log.info("receive test")
    case _ => log.info("received unknown message")
  }
}

object MyActor {
  def props = Props[MyActor]
}

case object Ping

case object Pong

class Pinger extends Actor with ActorLogging {
  var countDown = 100

  def receive = {
    case Pong =>
      log.info(s"${self.path.toString} received pong, count down ${countDown}")

      if (countDown > 0) {
        countDown -= 1
        sender ! Ping
      } else {
        sender ! PoisonPill
        self ! PoisonPill
      }
    case _ =>
      throw new IllegalArgumentException("unknown message")
  }
}

object Pinger {
  def props = Props(new Pinger)
}

class Ponger(pinger: ActorRef) extends Actor with ActorLogging {
  def receive = {
    case Ping =>
      log.info(s"${self.path.toString} received ping")
      pinger ! Pong
    case _ =>
      throw new IllegalArgumentException("unknown message.")
  }
}

object Ponger {
  def props(pinger: ActorRef) = Props(new Ponger(pinger))
}

class MyApp extends App {
  val system = ActorSystem("testSystem")
  val myActor = system.actorOf(MyActor.props, "myActor")
  myActor ! "test"
  myActor ! "foo"

  val pinger = system.actorOf(Pinger.props, "pinger")
  val ponger = system.actorOf(Ponger.props(pinger), "ponger")

  import system.dispatcher

  system.scheduler.scheduleOnce(500 millis) {
    ponger ! Ping
  }

  system.terminate()
}


