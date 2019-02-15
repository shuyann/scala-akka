package com.example.official.guide

import akka.actor._

import scala.io.StdIn

object PrintMyActorRefActor {
  def props: Props = Props(new PrintMyActorRefActor)
}

class PrintMyActorRefActor extends Actor {
  override def receive: Receive = {
    case "print" =>
      val secondRef = context.actorOf(Props.empty, "secondActor")
      println(s"Second: ${secondRef}")
  }
}

object ActorHierarchyExperiments extends App {
  val system = ActorSystem("testSystem")

  val firstRef = system.actorOf(PrintMyActorRefActor.props, "firstActor")
  println(s"First: ${firstRef}")
  firstRef ! "print"
  println(">>> Press ENTER to exit <<<")
  try StdIn.readLine()
  finally system.terminate()
}

