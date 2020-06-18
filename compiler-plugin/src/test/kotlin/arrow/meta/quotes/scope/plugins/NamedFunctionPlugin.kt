package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.CliPlugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.namedFunction

open class NamedFunctionPlugin : Meta {
    override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
      namedFunctionPlugin
    )
}

val Meta.namedFunctionPlugin : CliPlugin
    get() =
        "Named Function Scope Plugin" {
            meta(
              namedFunction(this, { true }) { (namedFunction, _) ->
                  Transform.replace(
                    replacing = namedFunction,
                    newDeclaration = identity(descriptor)
                  )
              }
            )
        }