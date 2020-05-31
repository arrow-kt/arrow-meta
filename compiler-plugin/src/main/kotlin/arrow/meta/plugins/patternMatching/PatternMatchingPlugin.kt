package arrow.meta.plugins.patternMatching

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke

val Meta.patternMatching: CliPlugin
  get() = "pattern matching" {
    meta(
      analysis(doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
       null
      },
      analysisCompleted = { project, module, bindingTrace, files ->
        bindingTrace.desugar
        null
      })
    )
  }
