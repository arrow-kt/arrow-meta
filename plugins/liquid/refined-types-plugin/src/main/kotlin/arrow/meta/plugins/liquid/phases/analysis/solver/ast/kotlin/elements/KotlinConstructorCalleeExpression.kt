package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ConstructorCalleeExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtConstructorCalleeExpression

fun interface KotlinConstructorCalleeExpression: ConstructorCalleeExpression {
  fun impl(): KtConstructorCalleeExpression
  override val typeReference: TypeReference?
    get() = impl().typeReference?.model()
  override val constructorReferenceExpression: SimpleNameExpression?
    get() = impl().constructorReferenceExpression?.model()
}
