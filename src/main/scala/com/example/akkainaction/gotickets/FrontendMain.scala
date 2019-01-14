package com.example.akkainaction.gotickets

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.Logging
import com.typesafe.config.ConfigFactory

object FrontendMain extends App with StartUp {
  val systemName = "frontend"
  val config = ConfigFactory.load(systemName)
  implicit val system = ActorSystem(systemName, config)

  val api = new RestApi() {
    val log = Logging(system.eventStream, systemName)
    implicit val requestTimeout = configuredRequestTimeout(config)

    implicit def executionContext = system.dispatcher

    def createPath(): String = {
      val config = ConfigFactory.load("frontend").getConfig("backend")
      val host = config.getString("host")
      val port = config.getInt("port")
      val protocol = config.getString("protocol")
      val appName = config.getString("system")
      val actorName = config.getString("actor")
      s"$protocol://$appName@$host:$port/$actorName"
    }

    def createBoxOffice: ActorRef = {
      val path = createPath()
      system.actorOf(Props(new RemoteLookupProxy(path)), "lookupBoxOffice")
    }
  }
  startUp(api.routes)
}
