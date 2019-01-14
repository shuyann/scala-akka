package com.example.akkainaction.gotickets

import akka.actor.{ActorSystem, ActorRef}
import akka.event.Logging

import com.typesafe.config.ConfigFactory


object SingleNodeMain extends App with StartUp {
  val systemName = "singlenode"
  val config = ConfigFactory.load(systemName)
  implicit val system = ActorSystem(systemName, config)

  val api = new RestApi() {
    val log = Logging(system.eventStream, "go-ticks")
    implicit val requestTimeout = configuredRequestTimeout(config)

    implicit val executionContext = system.dispatcher

    def createBoxOffice: ActorRef = system.actorOf(BoxOffice.props, BoxOffice.name)
  }

  startUp(api.routes)
}
