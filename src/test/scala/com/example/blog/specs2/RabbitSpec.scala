package com.example.blog.specs2

import org.specs2.mutable.SpecificationLike

class RabbitSpec extends SpecificationLike {

  import RabbitFixtures._

  "Rabbit" >> {
    "#jump" >> {
      "when name is foo" >> {
        "it returns 'foo jumped'" in {
          foo.jump must_== "foo jumped!"
        }
      }
      "when name is bar" >> {
        "it returns 'bar jumped'" in {
          bar.jump must_== "bar jumped!"
        }
      }
    }
    "#talk" >> {
      "when name is foo" >> {
        "it throws UnsupportedOperationException" in {
          foo.talk must throwAn[UnsupportedOperationException]
        }
      }
      "when name is bar" >> {
        "it throws UnsupportedOperationException" in {
          bar.talk must throwAn[UnsupportedOperationException]
        }
      }
    }
  }
  "TalkRabbit" >> {
    "#jump" >> {
      "when name is hoge" >> {
        "it returns 'hoge jumped!'" in {
          hoge.jump must_== "hoge jumped!"
        }
      }
    }
    "#talk" >> {
      "when name is hoge" >> {
        "it returns 'hoge talked!'" in {
          hoge.talk must_== "hoge talked!"
        }
      }
    }
  }
}

object RabbitFixtures {
  val foo = new Rabbit("foo")
  val bar = new Rabbit("bar")
  val hoge = new TalkRabbit("hoge")
}
