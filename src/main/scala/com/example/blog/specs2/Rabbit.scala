package com.example.blog.specs2

class Rabbit(name: String) {

  def jump: String = s"$name jumped!"

  def talk: String = throw new UnsupportedOperationException("Rabbit cannot talk")
}

class TalkRabbit(name: String) extends Rabbit(name) {

  override def talk: String = s"$name talked!"
}
