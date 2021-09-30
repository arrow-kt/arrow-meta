package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.DeclarationWithBody
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.DestructuringDeclaration
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Parameter
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtParameter

class KotlinParameter(val impl: KtParameter) : Parameter, KotlinCallableDeclaration {
  override fun impl(): KtParameter = impl
  override fun hasDefaultValue(): Boolean =
    impl().hasDefaultValue()

  override val defaultValue: Expression?
    get() = impl().defaultValue?.model()
  override val isMutable: Boolean
    get() = impl().isMutable
  override val isVarArg: Boolean
    get() = impl().isVarArg

  override fun hasValOrVar(): Boolean =
    impl.hasValOrVar()

  override val destructuringDeclaration: DestructuringDeclaration?
    get() = impl().destructuringDeclaration?.model()
  override val isLoopParameter: Boolean
    get() = impl().isLoopParameter
  override val isCatchParameter: Boolean
    get() = impl().isCatchParameter
  override val ownerFunction: DeclarationWithBody?
    get() = impl().ownerFunction?.model()
}
