package com.example.akkainaction.testdriven

import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, Suite}

// 全てのテストでActorSystemを開始して終了する必要があるため共通で利用できるtraitを定義
trait StopSystemAfterAll extends BeforeAndAfterAll {
  // このtraitはTestKitをミックスインしている時のみ利用することができる
  this: TestKit with Suite =>
  override protected def afterAll(): Unit = {
    super.afterAll()
    system.terminate()// 全てのテストを実行した後にTestKitが提供するsystemをシャットダウンする
  }
}
