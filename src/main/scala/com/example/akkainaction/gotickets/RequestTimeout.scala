package com.example.akkainaction.gotickets

import akka.util.Timeout
import com.typesafe.config.Config

trait RequestTimeout {

  import scala.concurrent.duration._

  def configuredRequestTimeout(config: Config): Timeout = {
    // load akka http server request-timeout
    val t = config.getString("akka.http.server.request-timeout")
    val d = Duration(t)
    FiniteDuration(d.length, d.unit)
  }

}
