package arrow.meta.quotes.scope.plugins

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.returnExpression

open class ReturnExpressionPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    returnExpressionPlugin
  )
}

val Meta.returnExpressionPlugin: CliPlugin
  get() =
    "Return Expression Scope Plugin" {
      meta(
        returnExpression(this, { true }) { expression ->
          Transform.replace(
            replacing = expression,
            newDeclaration = identity()
          )
        }
      )
    }