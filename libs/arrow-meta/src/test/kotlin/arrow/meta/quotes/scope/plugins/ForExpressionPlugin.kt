package arrow.meta.quotes.scope.plugins

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.forExpression

open class ForExpressionPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(forExpressionPlugin)
}

val Meta.forExpressionPlugin: CliPlugin
  get() = "For Expression Scope Plugin" {
    meta(
      forExpression(this, { true }) { expression ->
        Transform.replace(replacing = expression, newDeclaration = identity())
      }
    )
  }
