package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.typeAlias

open class TypeAliasPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
    typeAliasPlugin
  )
}

val Meta.typeAliasPlugin
  get() =
    "Type Alias Expression Scope Plugin" {
      meta(
        typeAlias({ true }) { element ->
          Transform.replace(
            replacing = element,
            newDeclaration = typeAlias("""$name""", `(typeParams)`.toStringList() , """$type""")
          )
        }
      )
    }