package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.PropertyDelegate
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtPropertyDelegate

fun interface KotlinPropertyDelegate : PropertyDelegate, KotlinElement {
  override fun impl(): KtPropertyDelegate
  override val expression: Expression?
    get() = impl().expression?.model()
}
