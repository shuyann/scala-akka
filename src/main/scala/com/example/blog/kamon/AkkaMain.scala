package com.example.blog.kamon

import akka.actor._
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

object AkkaMain extends App {

  val system = ActorSystem("testSystem")
  implicit val timeout = Timeout(5 seconds)
  // Part1
  val helloActor = system.actorOf(Props[HelloActor], "helloActor")
  helloActor ! "Hello"
  helloActor ! "foo"
  val reply = helloActor ? "How are you?"
  reply.onComplete {
    case Success(msg) => println(s"reply message: $msg")
    case Failure(_) => println("received message failure")
  }

  // Part2
  val parentActor = system.actorOf(Props[ParentActor], "parentActor")
  parentActor ! "Hello"
  parentActor ! ForChild("Hello")

  val replyChild = parentActor ? Child
  replyChild.onComplete {
    case Success(actor: ActorRef) => println(s"reply childactor path: ${actor.path.toString}")
    case Failure(_) => println("received message failure")
  }


  system.terminate()
}

class SimpleActor extends Actor with ActorLogging {
  def receive = {
    case "How are you?" => sender ! "I'm fine thank you"
    case "Nice to meet you" => sender ! "Nice to meet you too"
    case s: String => sender ! "Pardon?"
    case _ => log.warning("unknown message")
  }
}

class HelloActor extends Actor with ActorLogging {
  def receive = {
    case "Hello" => println("World")
    case "How are you?" => sender ! "I'm fine thank you!"
    case _ => log.warning("unknown message")
  }
}

case class ForChild(msg: String)

case object Child

class ChildActor extends Actor {
  def receive = {
    case msg: String => println(s"(Child): ${msg}")
    case _ => println("Unknown message")
  }
}

class ParentActor extends Actor {
  override def preStart(): Unit = context.actorOf(Props[ChildActor], "childActor")

  def receive = {
    case msg: String => println(s"(Parent): ${msg}")
    case ForChild(msg: String) => context.child("childActor").getOrElse(context.actorOf(Props[ChildActor])) ! msg
    case Child => sender ! context.child("childActor").getOrElse(context.actorOf(Props[ChildActor]))
    case _ => println("unknown message")
  }
}
