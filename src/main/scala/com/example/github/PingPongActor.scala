package com.example.github

import akka.actor._

case class PING()

case class PONG()

class PingPongActor extends Actor with ActorLogging {

  import context._

  var count = 0

  def receive = {
    case PING =>
      become(pong(self))
      self ! PONG
    case PONG =>
      become(ping(self))
      self ! PING
  }


  def pong(actor: ActorRef): Receive = {
    case PONG =>
      log.info("PONG")
      count += 1
      Thread.sleep(100)
      self ! PING
      become(ping(self))
      if (count > 10) context.stop(self)
  }

  def ping(actor: ActorRef): Receive = {
    case PING =>
      log.info("PING")
      count += 1
      Thread.sleep(100)
      self ! PONG
      become(pong(self))
      if (count > 10) context.stop(self)
  }
}
