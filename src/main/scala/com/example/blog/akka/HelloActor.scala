package com.example.blog.akka

import akka.actor.Actor

class HelloActor extends Actor {

  def receive = {
    case "Hello" => sender ! "World"
    case "How are you?" => sender ! "I'm fine thank you!"
    case _ => sender ! "unknown message."
  }
}
