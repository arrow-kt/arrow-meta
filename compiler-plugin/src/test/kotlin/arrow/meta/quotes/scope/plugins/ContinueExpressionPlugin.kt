package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.breakExpression

open class ContinueExpressionPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
    continueExpressionPlugin
  )
}

val Meta.continueExpressionPlugin
  get() =
    "Continue Expression Scope Plugin" {
      meta(
        breakExpression({ true }) { expression ->
          Transform.replace(
            replacing = expression,
            newDeclaration = """break$targetLabel""".`break`
          )
        }
      )
    }