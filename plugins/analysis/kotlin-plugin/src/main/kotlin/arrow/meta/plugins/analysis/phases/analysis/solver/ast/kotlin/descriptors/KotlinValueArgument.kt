package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ValueArgumentName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements.KotlinElement
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements.KotlinValueArgumentName
import org.jetbrains.kotlin.psi.KtElement

class KotlinValueArgument(val impl: org.jetbrains.kotlin.psi.ValueArgument) :
  ValueArgument, KotlinElement {
  override fun getArgumentName(): ValueArgumentName? =
    impl.getArgumentName()?.let { KotlinValueArgumentName(it) }

  override fun isNamed(): Boolean = impl.isNamed()

  override fun isExternal(): Boolean = impl.isExternal()

  override val argumentExpression: Expression?
    get() = impl.getArgumentExpression()?.model()
  override val isSpread: Boolean
    get() = impl.getSpreadElement() != null

  override fun impl(): KtElement = impl.asElement()
}
