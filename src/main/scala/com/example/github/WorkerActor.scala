package com.example.github

import akka.actor._

class WorkerActor extends Actor with ActorLogging {

  var state: Int = 0

  override def preStart(): Unit = {
    log.info(s"Starting WorkerActor instance hashcode # ${this.hashCode()}")
  }

  override def postStop: Unit = {
    log.info(s"Stopping WorkerActor instance hashcode # ${this.hashCode()}")
  }


  def receive = {
    case value: Int =>
      if (value <= 0) throw new ArithmeticException("Number equal or less than zero")
      else state = value
    case result: Result =>
      sender ! state
    case ex: NullPointerException => throw new NullPointerException("Null Value Passed")
    case _ => throw new IllegalArgumentException("Wrong Argument")
  }
}
