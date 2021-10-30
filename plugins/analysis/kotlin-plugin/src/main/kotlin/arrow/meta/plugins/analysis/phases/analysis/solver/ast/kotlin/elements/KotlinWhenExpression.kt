package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Property
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhenEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhenExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtWhenExpression

class KotlinWhenExpression(val impl: KtWhenExpression) : WhenExpression, KotlinExpression {

  override fun impl(): KtWhenExpression = impl

  override val entries: List<WhenEntry>
    get() = impl().entries.map { it.model() }
  override val subjectVariable: Property?
    get() = impl().subjectVariable?.model()
  override val subjectExpression: Expression?
    get() = impl().subjectExpression?.model()
  override val elseExpression: Expression?
    get() = impl().subjectExpression?.model()
}
