package arrow.meta.quotes.transform.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.`class`
import arrow.meta.quotes.namedFunction

val Meta.transformReplace: List<Plugin>
  get() = listOf(transformReplaceFunction, transformReplaceClass)

private val Meta.transformReplaceFunction: Plugin
  get() = "Transform Replace" {
    meta(
      namedFunction({ name == "transformReplace" }) { f ->
        Transform.replace(
          f,
          """ fun transformReplace() = println("Transform Replace") """.function.synthetic
        )
      }
    )
  }

private val Meta.transformReplaceClass: Plugin
  get() = "Transfor Replace Class" {
    meta(
      `class`({ name == "Foo" }) { c ->
        Transform.replace(
          c,
          """
          | class FooModified {
          |   fun generatedFun() = println("Generated function")
          | }
          """.`class`.synthetic
        )
      }
    )
  }
