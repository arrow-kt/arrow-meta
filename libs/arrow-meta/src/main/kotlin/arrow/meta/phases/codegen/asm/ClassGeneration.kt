package arrow.meta.phases.codegen.asm

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.backend.jvm.extensions.ClassGenerator
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.declarations.IrClass

/**
 * @see [ExtensionPhase]
 * @see [arrow.meta.dsl.codegen.asm.AsmSyntax]
 */
interface ClassGeneration : ExtensionPhase {
  @OptIn(ObsoleteDescriptorBasedAPI::class)
  fun CompilerContext.interceptClassGenerator(
    generator: ClassGenerator,
    declaration: IrClass?
  ): ClassGenerator
}
