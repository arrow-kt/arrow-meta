package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ConstantExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

open class KotlinConstantStringExpression(val impl: KtStringTemplateExpression) :
  ConstantExpression, KotlinExpression {

  init {
    require(!impl.hasInterpolation())
  }

  override fun impl(): KtStringTemplateExpression = impl
}
