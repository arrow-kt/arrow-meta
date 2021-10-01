package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ValueArgumentName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model

class KotlinValueArgumentName(val impl: org.jetbrains.kotlin.psi.ValueArgumentName) : ValueArgumentName {
  fun impl(): org.jetbrains.kotlin.psi.ValueArgumentName = impl
  override val asName: Name
    get() = Name(impl().asName.asString())
  override val referenceExpression: SimpleNameExpression?
    get() = impl().referenceExpression?.model()
}
