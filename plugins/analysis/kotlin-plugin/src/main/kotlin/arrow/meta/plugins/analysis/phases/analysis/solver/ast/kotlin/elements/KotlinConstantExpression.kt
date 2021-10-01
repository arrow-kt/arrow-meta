package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ConstantExpression
import org.jetbrains.kotlin.psi.KtConstantExpression

open class KotlinConstantExpression(val impl: KtConstantExpression) : ConstantExpression, KotlinExpression {
  override fun impl(): KtConstantExpression = impl
}
