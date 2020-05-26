package arrow.meta.quotes.scope.plugins

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.callExpression

open class CallExpressionPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    callExpressionPlugin
  )
}

val Meta.callExpressionPlugin: CliPlugin
  get() =
    "Call Expression Scope Plugin" {
      meta(
        callExpression({ true }) { expression ->
          Transform.replace(
            replacing = expression,
            newDeclaration = identity()
          )
        }
      )
    }
