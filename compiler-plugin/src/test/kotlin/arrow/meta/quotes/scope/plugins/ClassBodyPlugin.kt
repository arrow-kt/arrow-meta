package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.classBody

open class ClassBodyPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
    classBody
  )
}

private val Meta.classBody
  get() =
    "Class Body Scope Plugin" {
      meta(
        classBody({ true }) { c ->
          Transform.replace(
            replacing = c,
            newDeclaration =
            """
            | {
            |  $properties
            |
            |  $companionObjects
            |
            |  $functions
            |
            |  fun test() = 0
            | }
            """.classBody
          )
        }
      )
    }