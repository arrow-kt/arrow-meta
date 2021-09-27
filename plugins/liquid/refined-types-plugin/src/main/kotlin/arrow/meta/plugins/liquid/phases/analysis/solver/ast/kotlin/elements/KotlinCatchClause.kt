package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.CatchClause
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Parameter
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ParameterList
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtCatchClause

class KotlinCatchClause(val impl: KtCatchClause): CatchClause, KotlinElement {
  override fun impl(): KtCatchClause = impl
  override val parameterList: ParameterList?
    get() = impl().parameterList?.model()
  override val catchParameter: Parameter?
    get() = impl().catchParameter?.model()
  override val catchBody: Expression?
    get() = impl().catchBody?.model()
}
