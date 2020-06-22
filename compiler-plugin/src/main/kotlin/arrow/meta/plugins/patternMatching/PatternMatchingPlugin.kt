package arrow.meta.plugins.patternMatching

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.plugins.patternMatching.phases.analysis.resolveExpression
import arrow.meta.plugins.patternMatching.phases.analysis.wildcards
import arrow.meta.plugins.patternMatching.phases.resolve.diagnostics.suppressUnresolvedReference

val Meta.patternMatching: CliPlugin
  get() = "pattern matching" {
    meta(
      enableIr(),
      suppressDiagnostic { ctx.suppressUnresolvedReference(it) },
      analysis(
        doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
          null
        },
        analysisCompleted = { project, module, bindingTrace, files ->
          bindingTrace.resolveExpression { wildcards(project, it) }
          null
        }
      ),
      irDump()
    )
  }
