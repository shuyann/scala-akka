package com.example.akkainaction.gotickets

import scala.concurrent.Future

import akka.actor.ActorSystem
import akka.event.Logging
import akka.util.Timeout

import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer

import com.typesafe.config.{Config, ConfigFactory}
import scala.util.{Failure, Success}

object Main extends App
  with RequestTimeout {

  val config = ConfigFactory.load()
  // get host and port from config file
  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  implicit val system = ActorSystem()
  // bindAndHandle need implicit ExecutionContext parameter
  implicit val ec = system.dispatcher

  val api = new RestApi(system, requestTimeout(config)).routes // The RestApi provides a Route

  implicit val materializer = ActorMaterializer()
  // start http server from Akka HTTP
  val bindingFuture: Future[ServerBinding] =
    Http().bindAndHandle(api, host, port)

  val log = Logging(system.eventStream, "go-tickets")
  bindingFuture.map { serverBinding =>
    log.info(s"RestApi bound to ${serverBinding.localAddress} ")
  }.onComplete {
    case Success(_) =>
      log.info("Success to bind to {}:{}", host, port)
    case Failure(ex) =>
      log.error(ex, "Failed to bind to {}:{}!", host, port)
      system.terminate()
  }


}

trait RequestTimeout {

  import scala.concurrent.duration._

  def requestTimeout(config: Config): Timeout = {
    val t = config.getString("akka.http.server.request-timeout")
    val d = Duration(t)
    FiniteDuration(d.length, d.unit)
  }
}
