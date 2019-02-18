package com.example.official.actors

import akka.actor._

class StoppingActors extends Actor {
  val child: ActorRef = ???

  def receive = {
    case "interrupt-child" =>
      context.stop(child)
    case "done" =>
      context.stop(self)
  }
}


class StoppingActorsApp extends App {
  val system = ActorSystem("testSystem")
  val stoppingActor = system.actorOf(Props[StoppingActors])

  stoppingActor ! "interrupt-child"
  stoppingActor ! "done"

  system.terminate()
}
