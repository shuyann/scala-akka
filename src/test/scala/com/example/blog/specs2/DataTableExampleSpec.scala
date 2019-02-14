package com.example.blog.specs2

import org.specs2.mutable._
import org.specs2.mutable.Tables

class DataTableExampleSpec extends SpecificationLike with Tables {

  "add integers should just work in scala" >> {
    "x" | "y" | "expected" |
      1 ! 1 ! 2 |
      1 ! 2 ! 3 |
      2 ! 2 ! 4 |> {
      (x: Int, y: Int, expected: Int) => {
        x + y aka "add" must be_===(expected)
      }
    }
  }
  "sub integers should just work in scala" >> {
    "x" | "y" | "expected" |
      1 ! 1 ! 0 |
      2 ! 2 ! 0 |> {
      (x: Int, y: Int, expected: Int) => {
        x - y aka "sub" must be_===(expected)
      }
    }
  }
}
