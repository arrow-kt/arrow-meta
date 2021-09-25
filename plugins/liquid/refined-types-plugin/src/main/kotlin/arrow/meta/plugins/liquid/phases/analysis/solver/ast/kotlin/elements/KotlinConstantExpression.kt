package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ConstantExpression
import org.jetbrains.kotlin.psi.KtConstantExpression

fun interface KotlinConstantExpression : ConstantExpression, KotlinExpression {
  override fun impl(): KtConstantExpression
}
