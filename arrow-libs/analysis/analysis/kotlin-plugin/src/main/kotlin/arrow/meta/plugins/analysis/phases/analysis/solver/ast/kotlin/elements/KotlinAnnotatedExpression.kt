package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.AnnotatedExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtAnnotatedExpression

class KotlinAnnotatedExpression(val impl: KtAnnotatedExpression) :
  AnnotatedExpression, KotlinAnnotated, KotlinAnnotationsContainer {
  override fun impl(): KtAnnotatedExpression = impl

  override val baseExpression: Expression?
    get() = impl().baseExpression?.model()
}
