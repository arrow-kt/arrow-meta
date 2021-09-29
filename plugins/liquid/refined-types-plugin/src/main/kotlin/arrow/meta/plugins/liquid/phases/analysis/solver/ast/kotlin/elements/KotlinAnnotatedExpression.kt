
package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.AnnotatedExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtAnnotatedExpression

fun interface KotlinAnnotatedExpression : AnnotatedExpression, KotlinAnnotated, KotlinAnnotationsContainer {
  override fun impl(): KtAnnotatedExpression

  override val baseExpression: Expression?
    get() = impl().baseExpression?.model()
}
