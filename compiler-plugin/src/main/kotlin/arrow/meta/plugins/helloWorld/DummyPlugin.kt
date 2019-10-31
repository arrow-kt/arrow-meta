package arrow.meta.plugins.helloWorld

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.func

val Meta.helloWorld: Plugin
  get() =
    "Hello World" {
      meta(
        func({ name == "helloWorld" }) { c ->
          Transform.replace(
            replacing = c,
            newDeclaration =
            """|fun helloWorld(): Unit = 
               |  println("Hello ΛRROW Meta!")
               |""".function.synthetic
          )
        }
      )
    }
