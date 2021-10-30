package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.KotlinResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.types.KotlinType
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.psiUtil.lastBlockStatementOrThis
import org.jetbrains.kotlin.resolve.calls.callUtil.getType

fun interface KotlinExpression : Expression, KotlinElement {
  override fun impl(): KtExpression
  override fun type(context: ResolutionContext): Type? =
    if (context is KotlinResolutionContext)
      impl().getType(context.bindingContext)?.let { KotlinType(it) }
    else null

  override fun lastBlockStatementOrThis(): Expression = impl().lastBlockStatementOrThis().model()
}
