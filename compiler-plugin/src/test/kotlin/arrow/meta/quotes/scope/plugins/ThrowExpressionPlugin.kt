package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.throwExpression

open class ThrowExpressionPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
    throwExpressionPlugin
  )
}

val Meta.throwExpressionPlugin
  get() =
    "Throw Expression Scope Plugin" {
      meta(
        throwExpression({ true }) { expression ->
          Transform.replace(
            replacing = expression,
            newDeclaration = """throw $thrownExpression""".`throw`
          )
        }
      )
    }