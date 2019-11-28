package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.importDirective
import org.jetbrains.kotlin.resolve.ImportPath

open class ImportDirectivePlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
    importDirectivePlugin
  )
}

val Meta.importDirectivePlugin
  get() =
    "Import Directive Scope Plugin" {
      meta(
        importDirective({importPath != null}) { element ->
          Transform.replace(
            replacing = element,
            newDeclaration = importDirective(ImportPath(importedFqName, isAllUnder, alias))
          )
        }
      )
    }