package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.CliPlugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.catchClause

open class CatchClausePlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    catchClausePlugin
  )
}

val Meta.catchClausePlugin
  get() =
    "Catch Clause Scope Plugin" {
      meta(
        catchClause({ true }) { element ->
          Transform.replace(
            replacing = element,
            newDeclaration = identity()
          )
        }
      )
    }