package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.SuperTypeListEntry
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry

fun interface KotlinSuperTypeListEntry: SuperTypeListEntry {
  fun impl(): KtSuperTypeListEntry
  override val typeReference: TypeReference?
    get() = impl().typeReference?.model()
}
