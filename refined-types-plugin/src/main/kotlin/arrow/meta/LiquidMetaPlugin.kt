package arrow.meta

import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.liquid.liquidExpressions
import kotlin.contracts.ExperimentalContracts

open class LiquidMetaPlugin : Meta {
  @ExperimentalContracts
  override fun intercept(ctx: CompilerContext): List<CliPlugin> =
    listOf(
      liquidExpressions
    )
}
