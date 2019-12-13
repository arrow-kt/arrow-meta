package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.dotQualifiedExpression

open class DotQualifiedExpressionPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
    dotQualifiedExpressionPlugin
  )
}

val Meta.dotQualifiedExpressionPlugin
  get() =
    "Destructuring Declaration Scope Plugin" {
      meta(
        dotQualifiedExpression({ true }) { expression ->
          Transform.replace(
            replacing = expression,
            newDeclaration = identity()
          )
        }
      )
    }