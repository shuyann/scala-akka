package com.example.official.actors

// Actors are created by passing a Props instance into the actorOf factory method
// which is available on ActorSystem and ActorContext.
import akka.actor._

class FirstActor extends Actor with ActorLogging {

  val child = context.actorOf(Props[SecondActor])

  def receive = {
    case "child" =>
      log.info(s"child actor is ${child.toString}")
      child ! "call"
    case _ => sys.error("unknown message")
  }

}

class SecondActor extends Actor with ActorLogging {

  def receive = {
    case "call" => log.info("received message in second actor")
    case _ => sys.error("unknown message")
  }

}

