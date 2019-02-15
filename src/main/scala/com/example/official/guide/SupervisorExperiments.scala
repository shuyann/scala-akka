package com.example.official.guide

import akka.actor._

// supervisor strategy sample
object SupervisedActor {
  def props: Props = Props(new SupervisedActor)
}

class SupervisorActor extends Actor {
  val child = context.actorOf(SupervisedActor.props, "supervisedActor")

  override def receive: Receive = {
    case "failChild" => child ! "fail"
    case _ => sys.error("unknown message received.")
  }
}

class SupervisedActor extends Actor {
  override def preStart(): Unit = println("supervised actor started")

  override def postStop(): Unit = println("supervised actor stopped")

  override def receive: Receive = {
    case "fail" =>
      println("supervised actor fails now")
      throw new Exception("I failed!")
    case _ => sys.error("unknown message received.")
  }

}


object SupervisorExperiments extends App {
  val system = ActorSystem("testSystem")
  val supervisedActor = system.actorOf(SupervisedActor.props, "supervisedActor")
  supervisedActor ! "failChild"
  system.terminate()
}
