package com.example.github

import akka.actor._

object LocalActorSystem extends App {
  val system = ActorSystem("localActorSystem")
  val actor = system.actorOf(Props[LocalActor], "localActor")
  actor ! "foo"
  system.terminate()
}
