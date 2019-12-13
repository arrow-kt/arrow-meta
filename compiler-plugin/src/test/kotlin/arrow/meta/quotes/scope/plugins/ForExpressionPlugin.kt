package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.forExpression

open class ForExpressionPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
    blockExpressionPlugin,
    forExpressionPlugin
  )
}

val Meta.forExpressionPlugin
  get() =
    "For Expression Scope Plugin" {
      meta(
        forExpression({ true }) { expression ->
          Transform.replace(
            replacing = expression,
            newDeclaration = identity()
          )
        }
      )
    }