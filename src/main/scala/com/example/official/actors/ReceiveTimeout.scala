package com.example.official.actors

import akka.actor._
import scala.concurrent.duration._

class ReceiveTimeoutActor extends Actor with ActorLogging {
  // To set an initial delay
  context.setReceiveTimeout(30 milliseconds)

  def receive = {
    case "Hello" =>
      // To set in a response to a message
      context.setReceiveTimeout(100 milliseconds)
    case ReceiveTimeout =>
      // To turn it off
      context.setReceiveTimeout(Duration.Undefined)
      throw new RuntimeException("Receive timed out")
  }
}

class ReceiveTimeout extends App {
  val system = ActorSystem("testSystem")

  val timeoutActor = system.actorOf(Props[ReceiveTimeoutActor])
  timeoutActor ! "Hello"

  system.terminate()
}
