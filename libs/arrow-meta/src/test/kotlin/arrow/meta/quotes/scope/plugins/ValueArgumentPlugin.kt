package arrow.meta.quotes.scope.plugins

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.valueArgument

open class ValueArgumentPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    valueArgumentPlugin
  )
}

val Meta.valueArgumentPlugin
  get() =
    "Value Argument Scope Plugin" {
      meta(
        valueArgument(this, { true }) { arg ->
          Transform.replace(
            replacing = arg,
            newDeclaration = identity()
          )
        }
      )
    }
