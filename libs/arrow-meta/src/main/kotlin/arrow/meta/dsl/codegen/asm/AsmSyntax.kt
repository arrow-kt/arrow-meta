package arrow.meta.dsl.codegen.asm

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.codegen.asm.Codegen
import org.jetbrains.kotlin.codegen.ImplementationBodyCodegen
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall

/**
 * When the compiler goes to codegen, and IR is not enabled by default, it goes into the codegen
 * phase for the JVM where it uses the ASM libs to generate bytecode using the AST and associated
 * descriptors coming from the resolution phase.
 */
interface AsmSyntax {

  /**
   * The [codegen] function allows us to interact with [applyFunction], [applyProperty], and
   * [generateClassSyntheticParts]. Each one of these functions are invoked as the compiled and type
   * checked tree of [KtElement] and [DeclarationDescriptor] is processed for codegen. Here, we can
   * alter the bytecode emitted using the [Meta ASM DSL]. This DSL mirrors the [IR DSL] offering a
   * match + transform function that allows us to alter the codegen tree.
   */
  fun codegen(
    applyFunction:
      CompilerContext.(
        receiver: StackValue, resolvedCall: ResolvedCall<*>, c: ExpressionCodegenExtension.Context
      ) -> StackValue?,
    applyProperty:
      CompilerContext.(
        receiver: StackValue, resolvedCall: ResolvedCall<*>, c: ExpressionCodegenExtension.Context
      ) -> StackValue?,
    generateClassSyntheticParts: CompilerContext.(codegen: ImplementationBodyCodegen) -> Unit
  ): Codegen =
    object : Codegen {
      override fun CompilerContext.applyFunction(
        receiver: StackValue,
        resolvedCall: ResolvedCall<*>,
        c: ExpressionCodegenExtension.Context
      ): StackValue? = applyFunction(receiver, resolvedCall, c)

      override fun CompilerContext.applyProperty(
        receiver: StackValue,
        resolvedCall: ResolvedCall<*>,
        c: ExpressionCodegenExtension.Context
      ): StackValue? = applyProperty(receiver, resolvedCall, c)

      override fun CompilerContext.generateClassSyntheticParts(codegen: ImplementationBodyCodegen) =
        generateClassSyntheticParts(codegen)
    }
}
