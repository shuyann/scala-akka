package com.example.blog.specs2

import akka.actor.Actor
import com.example.blog.specs2.RabbitActor.SetName

class RabbitActor extends Actor {

  var name = ""

  def receive = {
    case SetName(value) => name = value
    case "jump" => sender ! s"$name jumped!"
    case "talk" => sender ! s"$name talked!"
    case _ => sys.error("unknown message.")
  }
}

object RabbitActor {

  case class SetName(value: String)

}
