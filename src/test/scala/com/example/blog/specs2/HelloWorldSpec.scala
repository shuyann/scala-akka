package com.example.blog.specs2

import org.specs2.mutable.SpecificationLike

class HelloWorldSpec extends SpecificationLike {

  "HelloWorld" >> {
    val target = "Hello World"
    "contains 11 characters" in {
      target must have size (11)
    }
    "start with 'Hello'" in {
      target must startWith("Hello")
    }
    "end with 'World'" in {
      target must endWith("World")
    }
  }
}
