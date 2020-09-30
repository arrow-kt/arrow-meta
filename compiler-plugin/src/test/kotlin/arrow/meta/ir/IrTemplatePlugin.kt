package arrow.meta.ir

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.ir.plugins.irModuleFragmentPlugin
import arrow.meta.phases.CompilerContext

open class IrPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    irModuleFragmentPlugin
  )
}