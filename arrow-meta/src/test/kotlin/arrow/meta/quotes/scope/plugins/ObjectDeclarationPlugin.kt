package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.CliPlugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.objectDeclaration

open class ObjectDeclarationPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    objectDeclarationPlugin
  )
}

val Meta.objectDeclarationPlugin
  get() = "Object Declaration Scope Plugin" {
    meta(
      objectDeclaration(this, { element.name == "Test" }) {
        Transform.replace(
          replacing = it.element,
          newDeclaration = identity()
        )
      }
    )
  }