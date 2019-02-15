package com.example.official.guide

import akka.actor._

object StartStopActor1 {
  def props: Props = Props(new StartStopActor1)
}

class StartStopActor1 extends Actor {
  override def preStart(): Unit = {
    println("first started")
    context.actorOf(StartStopActor2.props, "second")
  }

  override def postStop(): Unit = println("first stopped")

  override def receive: Receive = {
    // when stopped actor first , stopped child actors
    case "stop" => context.stop(self)
    case _ => sys.error("unknown message received.")
  }
}

object StartStopActor2 {
  def props: Props = Props(new StartStopActor2)
}

class StartStopActor2 extends Actor {
  override def preStart(): Unit = println("second started")

  override def postStop(): Unit = println("second stopped")

  // Actor.emptyBehavior is a useful placeholder when we don't
  // want to handle any messages in the actor.
  override def receive: Receive = Actor.emptyBehavior

}

object StartStopExperiments extends App {
  // preStart is invoked after the actor has started but before it processes its first message.
  // preStop is invoked just before the actor stops. No messages are processed after this point.
  val system = ActorSystem("testSystem")
  val first = system.actorOf(StartStopActor1.props, "first")
  first ! "stop"
  system.terminate()
}

