package arrow.meta.phases.codegen.asm

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.codegen.ClassBuilderFactory
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * @see [ExtensionPhase]
 * @see [arrow.meta.dsl.codegen.asm.AsmSyntax]
 */
interface ClassBuilder : ExtensionPhase {
  fun CompilerContext.interceptClassBuilder(
    interceptedFactory: ClassBuilderFactory,
    bindingContext: BindingContext,
    diagnostics: DiagnosticSink
  ): ClassBuilderFactory
}