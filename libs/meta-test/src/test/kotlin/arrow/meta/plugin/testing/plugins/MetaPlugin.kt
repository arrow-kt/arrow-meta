package arrow.meta.plugin.testing.plugins

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import kotlin.contracts.ExperimentalContracts

open class MetaPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(helloWorld)
}
