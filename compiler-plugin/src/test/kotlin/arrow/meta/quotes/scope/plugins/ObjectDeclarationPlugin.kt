package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.objectDeclaration

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