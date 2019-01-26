package com.example.akkainaction.faulttolerance

import akka.actor._
import akka.testkit._
import com.example.akkainaction.faulttolerance.LifeCycleHooks.{ForceRestart, SampleMessage}
import org.specs2.mutable._
import org.specs2.specification.{AfterAll, Scope}

class LifeCycleHooksSpec extends TestKit(ActorSystem("LifeCycleSpec"))
  with SpecificationLike
  with DefaultTimeout
  with AfterAll {

  override def afterAll(): Unit = {
    system.terminate()
  }

  "LifeCycleHooks" >> {
    "it valid response when log lifecycle hooks" in new Scope {
      val testActorRef = system.actorOf(Props[LifeCycleHooks], "LifeCycleHooks")
      watch(testActorRef) // watch actor stop
      testActorRef ! ForceRestart
      testActorRef.tell(SampleMessage, testActor)
      expectMsg(SampleMessage)
      system.stop(testActorRef)
      expectTerminated(testActorRef) // when actor stop , send Terminated message to testActor.
    }
  }
}
