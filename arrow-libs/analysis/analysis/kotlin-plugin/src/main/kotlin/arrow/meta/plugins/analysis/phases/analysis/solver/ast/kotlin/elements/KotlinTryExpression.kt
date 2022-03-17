package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BlockExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CatchClause
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FinallySection
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TryExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtTryExpression

class KotlinTryExpression(val impl: KtTryExpression) : TryExpression, KotlinExpression {
  override fun impl(): KtTryExpression = impl
  override val tryBlock: BlockExpression
    get() = impl().tryBlock.model()
  override val catchClauses: List<CatchClause>
    get() = impl().catchClauses.map { it.model() }
  override val finallyBlock: FinallySection?
    get() = impl().finallyBlock?.model()
}
