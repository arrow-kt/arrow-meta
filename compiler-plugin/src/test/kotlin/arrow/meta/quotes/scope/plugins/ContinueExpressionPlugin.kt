package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.CliPlugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.continueExpression

open class ContinueExpressionPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    continueExpressionPlugin
  )
}

val Meta.continueExpressionPlugin : CliPlugin
  get() =
    "Continue Expression Scope Plugin" {
      meta(
        continueExpression({ true }) { expression ->
          Transform.replace(
            replacing = expression,
            newDeclaration = identity()
          )
        }
      )
    }