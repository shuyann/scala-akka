package com.example.official.guide

import akka.actor._
import scala.io.StdIn

// entry point
object IotApp extends App {
  val system = ActorSystem("iotSystem")
  val supervisor = system.actorOf(IotSupervisor.props, "iotSupervisor")
  try StdIn.readLine
  finally system.terminate()
}

object IotSupervisor {
  // recommended pattern for creating actors by defining a props()
  def props: Props = Props(new IotSupervisor)
}

class IotSupervisor extends Actor with ActorLogging {
  override def preStart(): Unit = log.info("IoT Application started.")

  override def postStop(): Unit = log.info("IoT Application stopped.")

  // No need to handle any messages
  override def receive = Actor.emptyBehavior
}
