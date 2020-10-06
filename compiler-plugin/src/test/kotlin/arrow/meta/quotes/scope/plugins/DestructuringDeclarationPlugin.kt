package arrow.meta.quotes.scope.plugins

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.destructuringDeclaration

open class DestructuringDeclarationPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    destructuringDeclarationPlugin
  )
}

val Meta.destructuringDeclarationPlugin
  get() =
    "Destructuring Declaration Scope Plugin" {
      meta(
        destructuringDeclaration(this, { true }) { declaration ->
          Transform.replace(
            replacing = declaration,
            newDeclaration = identity()
          )
        }
      )
    }