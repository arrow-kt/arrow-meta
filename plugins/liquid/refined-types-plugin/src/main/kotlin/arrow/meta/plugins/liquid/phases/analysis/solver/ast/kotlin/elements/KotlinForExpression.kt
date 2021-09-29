package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.DestructuringDeclaration
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ForExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Parameter
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtForExpression

class KotlinForExpression(val impl: KtForExpression) : ForExpression, KotlinLoopExpression {
  override fun impl(): KtForExpression = impl
  override val loopParameter: Parameter?
    get() = impl().loopParameter?.model()
  override val destructuringDeclaration: DestructuringDeclaration?
    get() = impl().destructuringDeclaration?.model()
  override val loopRange: Expression?
    get() = impl().loopRange?.model()
}
