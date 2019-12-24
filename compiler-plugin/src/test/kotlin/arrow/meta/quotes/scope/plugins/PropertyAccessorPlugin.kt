package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.propertyAccessor

open class PropertyAccessorPlugin: Meta {
  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
    propertyAccessorPlugin
  )
}

val Meta.propertyAccessorPlugin
  get() = "Property accessor plugin" {
    meta(
      propertyAccessor({true}) { propertyAccessor ->
        Transform.replace(
          replacing = propertyAccessor,
          newDeclaration = identity()
        )
      }
    )
  }