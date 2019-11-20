package arrow.meta.plugins.helloWorld

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
          Transform.remove(
            removeIn = c,
            declaration = """ println("Hello!") """.expressionIn(c)
          )
        }
      )
    }
