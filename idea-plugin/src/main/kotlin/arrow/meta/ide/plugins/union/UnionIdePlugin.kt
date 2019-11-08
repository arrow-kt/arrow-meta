package arrow.meta.ide.plugins.union

import arrow.meta.Plugin
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.invoke
import arrow.meta.plugins.union.suppressTypeMismatch
import org.jetbrains.kotlin.diagnostics.Diagnostic

val IdeMetaPlugin.uniontypes: Plugin
  get() = "Union Types" {
    meta(
      addDiagnosticSuppressor(Diagnostic::suppressTypeMismatch)
    )
  }
