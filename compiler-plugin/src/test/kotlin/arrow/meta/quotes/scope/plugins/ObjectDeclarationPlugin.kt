package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.objectDeclaration

open class ObjectDeclarationPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
    objectDeclarationPlugin
  )
}

val Meta.objectDeclarationPlugin
  get() = "Object Declaration Scope Plugin" {
    meta(
      objectDeclaration({ name == "Test" }) { declaration ->
        Transform.replace(
          replacing = declaration,
          newDeclaration =
          """
          | $`@annotations` object $name ${superTypeList?.let { ": ${it.text}" } ?: ""} {
          |   $body
          | }
          | """.`object`)
      }
    )
  }