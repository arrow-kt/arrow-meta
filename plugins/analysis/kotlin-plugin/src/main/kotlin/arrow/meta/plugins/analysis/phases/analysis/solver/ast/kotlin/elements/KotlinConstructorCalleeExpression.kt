package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ConstructorCalleeExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtConstructorCalleeExpression

class KotlinConstructorCalleeExpression(override val impl: KtConstructorCalleeExpression) : ConstructorCalleeExpression, KotlinDefaultExpression(impl) {
  override fun impl(): KtConstructorCalleeExpression = impl
  override val typeReference: TypeReference?
    get() = impl().typeReference?.model()
  override val constructorReferenceExpression: SimpleNameExpression?
    get() = impl().constructorReferenceExpression?.model()
}
