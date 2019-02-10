package com.example.akkainaction.channels

import akka.actor._

class EchoActor extends Actor {
  def receive = {
    case msg: AnyRef =>
      sender ! msg
  }
}
