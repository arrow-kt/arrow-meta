package arrow.meta

import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.liquid.phases.analysisPhases

open class AnalysisPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> =
    listOf(
      "Arrow Analysis" {
        meta(analysisPhases())
      }
    )
}
