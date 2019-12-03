package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.returnExpression

open class ReturnExpressionPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
    returnExpressionPlugin
  )
}

val Meta.returnExpressionPlugin
  get() =
    "Return Expression Scope Plugin" {
      meta(
        returnExpression({ true }) { expression ->
          Transform.replace(
            replacing = expression,
            newDeclaration = """return $`return`""".`return`
          )
        }
      )
    }