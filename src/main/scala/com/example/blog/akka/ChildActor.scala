package com.example.blog.akka

import akka.actor.{Actor, Props}

case class ForChild(msg: String)

case class Child()


class ChildActor extends Actor {
  def receive = {
    case msg: String => sender ! s"(Child): ${msg}"
  }
}

class SupervisorActor extends Actor {
  override def preStart(): Unit = {
    context.actorOf(Props[ChildActor], "childActor")
  }

  def receive = {
    case msg: String => sender ! s"(Supervisor): ${msg}"
    case ForChild(msg: String) => context.child("childActor")
      .getOrElse(context.actorOf(Props[ChildActor], "childActor")) ! msg
    case Child => sender ! context.child("childActor").getOrElse(context.actorOf(Props[ChildActor]))
    case _ => sender ! "unknown message."
  }
}

