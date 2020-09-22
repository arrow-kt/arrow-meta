package arrow.meta.phases.codegen.ir

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrFile

/**
 * @see [ExtensionPhase]
 * @see [arrow.meta.dsl.codegen.ir.IrSyntax]
 */
interface IRGeneration : ExtensionPhase {

  fun CompilerContext.generate(
    file: IrFile,
    pluginContext: IrPluginContext
  )

}