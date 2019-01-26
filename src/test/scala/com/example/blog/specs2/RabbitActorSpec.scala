package com.example.blog.akka

import akka.actor._
import akka.testkit._
import com.example.blog.specs2.RabbitActor
import com.example.blog.specs2.RabbitActor.SetName
import org.specs2.mutable._
import org.specs2.specification.{AfterAll, Scope}

class RabbitActorSpec extends TestKit(ActorSystem("testRabbitActor"))
  with SpecificationLike
  with DefaultTimeout
  with ImplicitSender
  with Tables with AfterAll {

  def afterAll = system.terminate()

  trait TestRabbitActor extends Scope {
    val rabbitActor = system.actorOf(Props[RabbitActor])
  }

  "RabbitActor" >> {
    "#receive" >> {
      "it returns valid response" in new TestRabbitActor {
        val name = "foo"
        rabbitActor ! SetName(name)
        val jumpedName = "foo jumped!"
        val talkedName = "foo talked!"
        rabbitActor ! "jump"
        expectMsg[String](jumpedName)
        rabbitActor ! "talk"
        expectMsg[String](talkedName)
        ok
      }
    }
  }
}
