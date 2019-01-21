package com.example.blog.akka_http

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.{ByteString, Timeout}
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.Random


object Main extends App {

  implicit val system: ActorSystem = ActorSystem("httpSystem")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher
  val numbers = Source.fromIterator(() =>
    Iterator.continually(Random.nextInt()))

  case class Bid(userId: String, offer: Int)

  case object GetBids

  case class Bids(bids: List[Bid])

  class Auction extends Actor with ActorLogging {
    var bids = List.empty[Bid]

    def receive = {
      case bid@Bid(userId, offer) =>
        bids = bids :+ bid
        log.info(s"Bid complete: $userId, $offer")
      case GetBids => sender() ! Bids(bids)
      case _ => log.info("Invalid message")
    }
  }

  // these are from spray-json
  implicit val bidFormat = jsonFormat2(Bid)
  implicit val bidsFormat = jsonFormat1(Bids)

  val auction = system.actorOf(Props[Auction], "auction")

  // Routing DSL
  val route: Route =
    path("") {
      get {
        extractUri { uri =>
          complete(uri.toString())
        }
      } ~ post {
        complete("POST")
      } ~ put {
        complete("PUT")
      } ~ delete {
        complete("DELETE")
      }
    } ~
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      } ~
      path("random") {
        get {
          complete(
            HttpEntity(
              ContentTypes.`text/plain(UTF-8)`,
              // transform each number to a chunk of bytes
              numbers.map(n => ByteString(s"$n\n"))
            )
          )
        }
      } ~ path("auction") {
      put {
        parameter("bid".as[Int], "user") { (bid, user) =>
          // place a bid, fire-and-forget
          auction ! Bid(user, bid)
          complete((StatusCodes.Accepted, "bid placed"))
        }
      } ~
        get {
          implicit val timeout: Timeout = 5.seconds

          // query the actor for the current auction state
          val bids: Future[Bids] = (auction ? GetBids).mapTo[Bids]
          complete(bids.map(_.toString))
        }
    } ~ path("ping") {
      get {
        complete("pong")
      }
    }


  val host = "localhost"
  val port = 8080
  // Starting HTTP Server
  val bindingFuture = Http().bindAndHandle(route, host, port)
  bindingFuture.map { serverBinding =>
    println(s"Server online at ${serverBinding.localAddress}")
    println("Press RETURN to stop...")

    StdIn.readLine()

    serverBinding.unbind()
    serverBinding
  }
    .onComplete { _ =>
      system.terminate()
      println("System terminated.")
    }

}
