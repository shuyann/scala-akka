package com.example.blog.akka

import akka.actor.Actor

class HelloActor extends Actor {

  def receive = {
    case "Hello" => println("World")
    case "How are you?" => sender ! "I'm fine thank you!"
    case _ => println("unknown message.")
  }
}
