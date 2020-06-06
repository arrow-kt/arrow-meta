package arrow.meta.quotes.scope.plugins

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.property

open class PropertyPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    propertyPlugin
  )
}

val Meta.propertyPlugin
  get() = "Property scope plugin" {
    meta(
      property(this, { true }) { property ->
        Transform.replace(
          replacing = property,
          newDeclaration = identity()
        )
      }
    )
  }