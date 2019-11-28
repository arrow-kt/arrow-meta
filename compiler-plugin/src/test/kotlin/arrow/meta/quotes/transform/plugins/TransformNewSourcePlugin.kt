package arrow.meta.quotes.transform.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.`class`

val Meta.transformNewSource: Plugin
  get() = "Transform New Source" {
    meta(
      `class`({ name == "NewSource" }) {
        Transform.newSources(
          """
          |  class ${name}_Generated {
          |    fun sayHi() = println("Hi")
          |  }
          """.file("${name}_Generated")
        )
      }
    )
  }