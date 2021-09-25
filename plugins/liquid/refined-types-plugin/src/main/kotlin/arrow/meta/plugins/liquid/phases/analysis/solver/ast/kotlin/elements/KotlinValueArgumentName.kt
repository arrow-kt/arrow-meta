package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ValueArgumentName
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtValueArgumentName

fun interface KotlinValueArgumentName: ValueArgumentName {
  fun impl(): KtValueArgumentName
  override val asName: Name
    get() = Name(impl().asName.asString())
  override val referenceExpression: SimpleNameExpression?
    get() = impl().referenceExpression.model()
}
