package arrow.meta.quotes.scope.plugins

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.breakExpression

open class BreakExpressionPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(breakExpressionPlugin)
}

val Meta.breakExpressionPlugin: CliPlugin
  get() = "Break Expression Scope Plugin" {
    meta(
      breakExpression(this, { true }) { expression ->
        Transform.replace(replacing = expression, newDeclaration = identity())
      }
    )
  }
