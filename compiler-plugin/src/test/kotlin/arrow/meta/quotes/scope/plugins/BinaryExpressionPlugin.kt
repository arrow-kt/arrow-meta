package arrow.meta.quotes.scope.plugins

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.binaryExpression

open class BinaryExpressionPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    binaryExpressionPlugin
  )
}

val Meta.binaryExpressionPlugin: CliPlugin
  get() =
    "Binary Expression Scope Plugin" {
      meta(
        binaryExpression(this, { true }) { expression ->
          Transform.replace(
            replacing = expression,
            newDeclaration = identity()
          )
        }
      )
    }