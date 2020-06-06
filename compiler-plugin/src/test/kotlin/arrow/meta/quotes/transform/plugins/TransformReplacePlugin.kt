package arrow.meta.quotes.transform.plugins

import arrow.meta.Meta
import arrow.meta.CliPlugin
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.classDeclaration
import arrow.meta.quotes.namedFunction

val Meta.transformReplace: List<CliPlugin>
  get() = listOf(transformReplaceFunction, transformReplaceClass)

private val Meta.transformReplaceFunction: CliPlugin
  get() = "Transform Replace Function" {
    meta(
      namedFunction(this, { name == "transformReplace" }) { f ->
        Transform.replace(
          f,
          """ fun transformReplace() = println("Transform Replace") """.function.syntheticScope
        )
      }
    )
  }

private val Meta.transformReplaceClass: CliPlugin
  get() = "Transform Replace Class" {
    meta(
      classDeclaration(this, { name == "Foo" }) { c ->
        Transform.replace(
          c,
          """
          | class FooModified {
          |   fun generatedFun() = println("Generated function")
          | }
          """.`class`.syntheticScope
        )
      }
    )
  }
