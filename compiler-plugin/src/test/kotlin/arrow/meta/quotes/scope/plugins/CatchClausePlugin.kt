package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.catchClause

open class CatchClausePlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
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
            newDeclaration = """catch ($parameter) $`{ catchBody }`""".catch
          )
        }
      )
    }