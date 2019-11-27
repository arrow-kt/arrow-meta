package arrow.meta.quotes.scope.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.scope.plugins.objectDeclarationPlugin

open class ScopeMetaPlugin : Meta {
  
  override fun intercept(ctx: CompilerContext): List<Plugin> = listOf(
    objectDeclarationPlugin
  )
}