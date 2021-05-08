package arrow.meta.quotes.scope.plugins

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.whenExpression

open class WhenExpressionPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    whenExpressionPlugin
  )
}

val Meta.whenExpressionPlugin
  get() = "When Expression Scope Plugin" {
    meta(
      whenExpression(this, { true }) { e ->
        Transform.replace(
          replacing = e,
          newDeclaration = identity()
        )
      }
    )
  }