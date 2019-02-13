package com.example.github

import akka.actor._

object JDBCSettingActorSystem extends App {
  val system = ActorSystem("jdbcsetting")
  val actor = system.actorOf(Props[JDBCSettingActor])
  actor ! "get"
  system.terminate()
}
