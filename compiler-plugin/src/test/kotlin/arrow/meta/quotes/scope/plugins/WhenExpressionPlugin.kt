package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.whenExpression

open class WhenExpressionPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
    whenExpressionPlugin
  )
}

val Meta.whenExpressionPlugin
  get() = "When Expression Scope Plugin" {
    meta(
      whenExpression({ true }) { e ->
        Transform.replace(
          replacing = e,
          newDeclaration =
          """
            | when $`(expression)`{ 
            |   $entries
            | }""".`when`
        )
      }
    )
  }