package com.example.akkainaction.gotickets

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object BackendMain extends App with StartUp {
  val systemName = "backend"
  val config = ConfigFactory.load(systemName)
  val system = ActorSystem(systemName, config)
  implicit val requestTimeout = configuredRequestTimeout(config)
  system.actorOf(BoxOffice.props, BoxOffice.name)
}
