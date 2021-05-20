package arrow.meta

import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.macros.inlineMacrosPlugin
import kotlin.contracts.ExperimentalContracts

open class InlineMacrosMetaPlugin : Meta {
  @ExperimentalContracts
  override fun intercept(ctx: CompilerContext): List<CliPlugin> =
    listOf(
      inlineMacrosPlugin
    )
}
