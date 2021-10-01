package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DelegatedSuperTypeEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtDelegatedSuperTypeEntry

fun interface KotlinDelegatedSuperTypeEntry : DelegatedSuperTypeEntry, KotlinSuperTypeListEntry {
  override fun impl(): KtDelegatedSuperTypeEntry
  override val delegateExpression: Expression?
    get() = impl().delegateExpression?.model()
}
