package com.example.github

import akka.actor._

object PingPongActorSystem extends App {
  val system = ActorSystem("pingpong")
  val actor = system.actorOf(Props[PingPongActor])
  actor ! PING
  Thread.sleep(2000)
  system.terminate()
}
