package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.DestructuringDeclaration
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ForExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Parameter
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtForExpression

fun interface KotlinForExpression : ForExpression, KotlinLoopExpression {
  override fun impl(): KtForExpression
  override val loopParameter: Parameter?
    get() = impl().loopParameter?.model()
  override val destructuringDeclaration: DestructuringDeclaration?
    get() = impl().destructuringDeclaration?.model()
  override val loopRange: Expression?
    get() = impl().loopRange?.model()
}
