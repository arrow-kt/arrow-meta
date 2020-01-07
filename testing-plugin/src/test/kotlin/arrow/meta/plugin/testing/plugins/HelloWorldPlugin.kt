package arrow.meta.plugin.testing.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.namedFunction

val Meta.helloWorld: Plugin
  get() =
    "Hello World" {
      meta(
        namedFunction({ name == "helloWorld" }) { c ->
          Transform.replace(
            replacing = c,
            newDeclaration =
            """| 
               | fun helloWorld(): String = 
               |   "Hello ΛRROW Meta!"
               |   
               |""".function.syntheticScope
          )
        }
      )
    }