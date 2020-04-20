package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.CliPlugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.whileExpression

open class WhileExpressionPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    whileExpressionPlugin
  )
}

val Meta.whileExpressionPlugin
  get() = "While Expression Scope Plugin" {
    meta(
      whileExpression({ true }) { e ->
        Transform.replace(
          replacing = e,
          newDeclaration = identity()
        )
      }
    )
  }