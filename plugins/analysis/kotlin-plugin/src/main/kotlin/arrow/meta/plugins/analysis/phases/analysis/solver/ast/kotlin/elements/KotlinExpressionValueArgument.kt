package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ValueArgumentName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model

open class KotlinExpressionValueArgument(open val impl: org.jetbrains.kotlin.psi.KtValueArgument) : ValueArgument, KotlinElement {
  override fun impl(): org.jetbrains.kotlin.psi.KtValueArgument = impl

  override fun getArgumentName(): ValueArgumentName? =
    impl().getArgumentName()?.model()

  override fun isNamed(): Boolean =
    impl().isNamed()

  override fun isExternal(): Boolean =
    impl().isExternal()

  override val argumentExpression: Expression?
    get() = impl().getArgumentExpression()?.model()
  override val isSpread: Boolean
    get() = impl().isSpread
}
