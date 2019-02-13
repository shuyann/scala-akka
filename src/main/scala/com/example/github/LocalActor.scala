package com.example.github

import akka.actor._
import akka.pattern._
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

class LocalActor extends Actor with ActorLogging {
  val remoteActor = context.actorOf(Props[LocalRemoteActor])
  implicit val timeout: Timeout = 5 seconds

  def receive = {
    case msg => {
      log.info(s"LocalActor received: ${msg}")
      val result = (remoteActor ? msg).mapTo[String]
      result.onComplete {
        case Success(v) => log.info(s"Success: ${v}")
        case Failure(e) => throw e
      }
    }
  }

}

class LocalRemoteActor extends Actor with ActorLogging {
  def receive = {
    case msg =>
      log.info(s"LocalRemoteActor received: ${msg}")
      sender ! "done"
  }
}
