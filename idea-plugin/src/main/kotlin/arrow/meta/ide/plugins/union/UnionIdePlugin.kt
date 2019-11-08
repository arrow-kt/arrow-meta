package arrow.meta.ide.plugins.union


import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.IdePlugin
import arrow.meta.ide.invoke
import arrow.meta.plugins.union.suppressTypeMismatchOnNullableReceivers
import org.jetbrains.kotlin.diagnostics.Diagnostic

val IdeMetaPlugin.uniontypes: IdePlugin
  get() = "Union Types" {
    meta(
      addDiagnosticSuppressor(Diagnostic::suppressTypeMismatchOnNullableReceivers)
    )
  }
