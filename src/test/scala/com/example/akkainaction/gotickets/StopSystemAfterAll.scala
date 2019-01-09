package com.example.akkainaction.gotickets

import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, Suite}

// 全てのテストでActorSystemを開始して終了する必要があるため共通で利用できるtraitを定義
trait StopSystemAfterAll extends BeforeAndAfterAll {
  this: TestKit with Suite =>
  override protected def afterAll(): Unit = {
    super.afterAll()
    system.terminate()
  }
}
