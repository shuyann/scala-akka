package com.example.akkainaction.gotickets

import scala.concurrent.Future

import akka.actor.ActorSystem
import akka.event.Logging

import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route

import akka.stream.ActorMaterializer
import scala.util.{Success, Failure}

trait StartUp extends RequestTimeout {

  def startUp(api: Route)(implicit system: ActorSystem) = {
    // Gets the host and port from the configuration.
    val host = system.settings.config.getString("http.host")
    val port = system.settings.config.getInt("http.port")
    startHttpServer(api, host, port)
  }

  def startHttpServer(api: Route, host: String, port: Int)(implicit system: ActorSystem) = {
    implicit val ec = system.dispatcher // bindAndHandle requires an implicit ExecutionContext
    implicit val materializer = ActorMaterializer()
    val bindingFuture: Future[ServerBinding] =
      Http().bindAndHandle(api, host, port) // Starts the HTTP server

    val log = Logging(system.eventStream, "go-ticks")
    bindingFuture.map { serverBinding =>
      log.info(s"RestApi bound to ${serverBinding.localAddress}")
    }.onComplete {
      case Success(_) =>
      case Failure(ex) =>
        log.error(ex, "Failed to bind to {}:{}!", host, port)
        system.terminate()
    }
  }
}
