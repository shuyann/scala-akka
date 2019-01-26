package com.example.akkainaction.testdriven

import akka.actor._
import akka.testkit._
import akka.util.Timeout
import akka.pattern.ask
import org.specs2.mutable._
import org.specs2.specification.{AfterAll, Scope}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class EchoActorSpec extends TestKit(ActorSystem("testsystem"))
  with SpecificationLike
  with DefaultTimeout
  with ImplicitSender
  with AfterAll {

  def afterAll = system.terminate()

  trait TestEchoActor extends Scope {
    val echoActor = system.actorOf(Props[EchoActor])
  }

  "EchoActor" >> {
    "#receive" >> {
      "it reply same message receive" in new TestEchoActor {
        implicit val timeout = Timeout(3 seconds)
        implicit val ec = system.dispatcher
        val echo = system.actorOf(Props[EchoActor])
        val future = echo.ask("some message")
        future.onComplete {
          case Failure(_) => ko
          case Success(msg) => ok
        }
        Await.ready(future, timeout.duration)
      }

      "it reply same message receive without ask" in new TestEchoActor {
        val echo = system.actorOf(Props[EchoActor])
        echo ! "some message"
        expectMsg[String]("some message")
        ok
      }
    }
  }
}


