package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.namedFunction

open class NamedFunctionPlugin : Meta {
    override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
      namedFunctionPlugin
    )
}

val Meta.namedFunctionPlugin
    get() =
        "Named Function Scope Plugin" {
            meta(
              namedFunction({ true }) { namedFunction ->
                  Transform.replace(
                    replacing = namedFunction,
                    newDeclaration = identity()
                  )
              }
            )
        }