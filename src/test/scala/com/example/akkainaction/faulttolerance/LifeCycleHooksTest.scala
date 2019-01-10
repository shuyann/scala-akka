package com.example.akkainaction.faulttolerance

import akka.actor._
import akka.testkit.TestKit
import com.example.akkainaction.faulttolerance.LifeCycleHooks.{ForceRestart, SampleMessage}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class LifeCycleHooksTest extends TestKit(ActorSystem("LifeCycleTest")) with WordSpecLike with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    system.terminate() // system terminate when after all tests.
  }

  "The Child" must {
    "log lifecycle hooks" in {
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
