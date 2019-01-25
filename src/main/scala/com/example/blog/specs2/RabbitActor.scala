package com.example.blog.specs2

import akka.actor.Actor
import com.example.blog.specs2.RabbitActor.SetName

class RabbitActor extends Actor {

  // name is mutable
  var name = ""

  def receive = {
    case SetName(value) => name = value
    case "jump" => s"$name jumped!"
    case "talk" => s"$name talked!"
  }
}

object RabbitActor {

  // set name message object.
  case class SetName(value: String)

}
