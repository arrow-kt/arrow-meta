package arrow.meta.quotes.scope.plugins

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.typeReference

open class TypeReferencePlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    typeReferencePlugin
  )
}

val Meta.typeReferencePlugin
  get() =
    "Type Reference Scope Plugin" {
      meta(
        typeReference(this, { true }) { expression ->
          Transform.replace(
            replacing = expression,
            newDeclaration = identity()
          )
        }
      )
    }
