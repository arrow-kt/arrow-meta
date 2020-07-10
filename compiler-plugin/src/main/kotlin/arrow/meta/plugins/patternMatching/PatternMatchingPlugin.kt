package arrow.meta.plugins.patternMatching

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.plugins.patternMatching.phases.analysis.patternExpressionAnalysis
import arrow.meta.plugins.patternMatching.phases.ir.irPatternMatching
import arrow.meta.plugins.patternMatching.phases.resolve.diagnostics.suppressUnresolvedReference

val Meta.patternMatching: CliPlugin
  get() =
    "Pattern Matching Plugin" {
      meta(
        enableIr(),
        patternExpressionAnalysis(),
        suppressDiagnostic { ctx.suppressUnresolvedReference(it) },
        IrGeneration { compilerContext, file, backendContext, bindingContext ->
          irPatternMatching(compilerContext, file, backendContext, bindingContext)
        },
        irDump()
      )
    }
