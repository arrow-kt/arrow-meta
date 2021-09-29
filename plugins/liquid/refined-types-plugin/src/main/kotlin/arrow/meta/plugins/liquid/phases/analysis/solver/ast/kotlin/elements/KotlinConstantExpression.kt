package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ConstantExpression
import org.jetbrains.kotlin.psi.KtConstantExpression

open class KotlinConstantExpression(val impl: KtConstantExpression) : ConstantExpression, KotlinExpression {
  override fun impl(): KtConstantExpression = impl
}
