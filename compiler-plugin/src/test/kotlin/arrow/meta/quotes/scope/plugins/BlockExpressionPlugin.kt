package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.blockExpression

open class BlockExpressionPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
    blockExpressionPlugin
  )
}

val Meta.blockExpressionPlugin
  get() =
    "Block Expression Scope Plugin" {
      meta(
        blockExpression({ true }) { expression ->
          Transform.replace(
            replacing = expression,
            newDeclaration = identity()
          )
        }
      )
    }