package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SuperTypeListEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry

fun interface KotlinSuperTypeListEntry : SuperTypeListEntry, KotlinElement {
  override fun impl(): KtSuperTypeListEntry
  override val typeReference: TypeReference?
    get() = impl().typeReference?.model()
}
