package com.example.github

import akka.actor._
import com.typesafe.config.ConfigFactory

class JDBCSettingActor extends Actor with ActorLogging {
  def receive = {
    case _ =>
      val jdbcConfig = ConfigFactory.load.getConfig("jdbc")
      val dbUrl = jdbcConfig.getString("connection.db.url")
      val dbName = jdbcConfig.getString("connection.db.dbname")
      val dbDriver = jdbcConfig.getString("connection.db.driver")
      val dbUsername = jdbcConfig.getString("connection.db.username")
      val dbPassword = jdbcConfig.getString("connection.db.userpassword")
      log.info(s"${dbUrl}:${dbName}:${dbDriver}:${dbUsername}:${dbPassword}")
  }
}
