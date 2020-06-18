package arrow.meta.plugin.testing.plugins

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.namedFunction

val Meta.helloWorld: CliPlugin
  get() =
    "Hello World" {
      meta(
        namedFunction(this, { element.name == "helloWorld" }) { (c, _) ->
          Transform.replace(
            replacing = c,
            newDeclaration =
            """| 
               | fun helloWorld(): String = 
               |   "Hello ΛRROW Meta!"
               |   
               |""".function(descriptor).syntheticScope
          )
        }
      )
    }