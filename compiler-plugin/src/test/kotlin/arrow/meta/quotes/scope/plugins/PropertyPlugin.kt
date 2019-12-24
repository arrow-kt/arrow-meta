package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.property

open class PropertyPlugin: Meta {
  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
    propertyPlugin
  )
}

val Meta.propertyPlugin
  get() = "Property scope plugin" {
    meta(
      property({ true }) { property ->
        Transform.replace(
          replacing = property,
          newDeclaration = identity()
        )
      }
    )
  }