package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.CliPlugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.finallySection

open class FinallySectionPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    finallySectionPlugin
  )
}

val Meta.finallySectionPlugin
  get() =
    "Finally Section Scope Plugin" {
      meta(
        finallySection(this, { true }) { expression ->
          Transform.replace(
            replacing = expression,
            newDeclaration = identity()
          )
        }
      )
    }