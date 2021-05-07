package arrow.meta.quotes.scope.plugins

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.importDirective

open class ImportDirectivePlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    importDirectivePlugin
  )
}

val Meta.importDirectivePlugin
  get() =
    "Import Directive Scope Plugin" {
      meta(
        importDirective(this, { importPath != null }) { element ->
          Transform.replace(
            replacing = element,
            newDeclaration = identity()
          )
        }
      )
    }