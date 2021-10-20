package arrow.meta

import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.analysis.phases.analysisPhases
import kotlin.contracts.ExperimentalContracts

open class AnalysisPlugin : Meta {
  @ExperimentalContracts
  override fun intercept(ctx: CompilerContext): List<CliPlugin> =
    listOf(
      "Arrow Analysis" {
        meta(analysisPhases())
      }
    )
}
