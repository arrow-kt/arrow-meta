package arrow.meta.ide.plugins.patternMatching

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.IdePlugin
import arrow.meta.ide.invoke
import arrow.meta.plugins.patternMatching.phases.resolve.diagnostics.suppressExpressionExpectedPackageFound
import arrow.meta.plugins.patternMatching.phases.resolve.diagnostics.suppressUnderscoreUsageWithoutBackticks
import arrow.meta.plugins.patternMatching.phases.resolve.diagnostics.suppressUnresolvedReference

val IdeMetaPlugin.patternMatchingIde: IdePlugin
  get() = "Pattern Matching IDE" {
    meta(
      enableIr(),
      addDiagnosticSuppressorWithCtx { suppressUnresolvedReference(it) },
      addDiagnosticSuppressorWithCtx { suppressUnderscoreUsageWithoutBackticks(it) },
      addDiagnosticSuppressorWithCtx { suppressExpressionExpectedPackageFound(it) }
    )
  }
