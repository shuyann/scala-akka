package com.example.akkainaction.structure

import java.util.Date
import akka.actor.{ActorSystem, Props}
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit, TestProbe}
import org.specs2.mutable.SpecificationLike
import org.specs2.specification.{AfterAll, Scope}

import scala.concurrent.duration._

class AggregatorSpec extends TestKit(ActorSystem("testAggregator"))
  with SpecificationLike
  with DefaultTimeout
  with ImplicitSender
  with AfterAll {

  def afterAll = system.terminate()

  override val timeout = 2 seconds

  trait TargetActor extends Scope {
    val endProbe = TestProbe()
    val actorRef = system.actorOf(Props(new Aggregator(2 seconds, endProbe.ref)))
  }

  object Fixtures {
    val photoStr = ImageProcessing.createPhotoString(new Date(), 60)
    val msg1 = PhotoMessage("id1", photoStr, Some(new Date()), None)
    val msg2 = PhotoMessage("id1", photoStr, None, Some(60))
    val combinedMessage = PhotoMessage("id1", photoStr, msg1.creationTime, msg2.speed)
  }

  "ScatterGather" >> {
    import Fixtures._
    "when aggregate two messages" >> {
      "it returns valid response" in new TargetActor {
        actorRef ! msg1
        actorRef ! msg2
        endProbe.expectMsg(combinedMessage)
        ok
      }
    }
    "when send message after timeout" >> {
      "it returns valid response" in new TargetActor {
        actorRef ! msg1
        endProbe.expectMsg(msg1)
        ok
      }
    }
    "when aggregate two messages restarting" >> {
      "it returns valid response" in new TargetActor {
        actorRef ! msg1
        actorRef ! new IllegalStateException("restart")
        actorRef ! msg2
        endProbe.expectMsg(combinedMessage)
        ok
      }
    }
  }
}
