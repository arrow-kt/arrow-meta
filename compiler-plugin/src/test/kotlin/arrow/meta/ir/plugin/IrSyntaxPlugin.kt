package arrow.meta.ir.plugin

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.ir.syntax.irVisit
import arrow.meta.phases.CompilerContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty

open class IrSyntaxPlugin : Meta {
  override fun intercept(ctx: CompilerContext): List<CliPlugin> = listOf(
    "IrSyntaxPlugin" {
      meta(
        irModuleFragment(irVisit(IrModuleFragment::class.java)),
        irProperty(irVisit(IrProperty::class.java))
      )
    }
  )
}