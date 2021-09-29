package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.NullExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.psiUtil.isNull

class KotlinNullExpression(impl: KtConstantExpression) : NullExpression, KotlinConstantExpression(impl) {
  init {
    require(impl.isNull())
  }
}
