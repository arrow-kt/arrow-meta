package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.ifExpression

open class IfExpressionPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
    ifExpressionPlugin
  )
}

val Meta.ifExpressionPlugin
  get() =
    "If Expression Scope Plugin" {
      meta(
        ifExpression({ true }) { expression ->
          Transform.replace(
            replacing = expression,
            newDeclaration = identity()
          )
        }
      )
    }