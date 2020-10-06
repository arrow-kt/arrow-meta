package arrow.meta.quotes.scope.plugins

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Transform
import arrow.meta.quotes.whenEntry

open class WhenEntryPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    whenEntryPlugin
  )
}

val Meta.whenEntryPlugin
  get() = "When Entry Scope Plugin" {
    meta(
      whenEntry(this, { true }) { e ->
        Transform.replace(
          replacing = e,
          newDeclaration = identity()
        )
      }
    )
  }