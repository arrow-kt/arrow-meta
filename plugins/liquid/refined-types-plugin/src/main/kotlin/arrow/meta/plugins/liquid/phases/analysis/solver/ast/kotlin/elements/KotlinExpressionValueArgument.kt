package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ExpressionValueArgumentName
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ValueArgument
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ValueArgumentName
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model

fun interface KotlinExpressionValueArgument : ValueArgument, KotlinElement {
  override fun impl(): org.jetbrains.kotlin.psi.KtValueArgument
  override val argumentExpression: Expression?
    get() = impl().getArgumentExpression()?.model()
  override val argumentName: ExpressionValueArgumentName?
    get() = impl().getArgumentName()?.model()
  override val isSpread: Boolean
    get() = impl().isSpread

  override fun getArgumentName(): ValueArgumentName? =
    impl().getArgumentName()?.model()

  override fun isNamed(): Boolean =
    impl().isNamed()

  override fun asElement(): Element =
    impl().asElement().model()

  override fun isExternal(): Boolean =
    impl().isExternal()
}
