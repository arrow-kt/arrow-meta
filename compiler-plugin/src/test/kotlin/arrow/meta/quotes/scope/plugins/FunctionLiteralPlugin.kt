package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.functionLiteral

open class FunctionLiteralPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
    functionLiteralPlugin
  )
}

val Meta.functionLiteralPlugin
  get() =
    "Function Literal Scope Plugin" {
      meta(
        functionLiteral({ true }) { expression ->
          Transform.replace(
            replacing = expression,
            newDeclaration = identity()
          )
        }
      )
    }