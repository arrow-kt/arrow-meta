package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SelfType
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtSelfType

fun interface KotlinSelfType : SelfType, KotlinTypeElement {
  override fun impl(): KtSelfType
  override val typeArgumentsAsTypes: List<TypeReference>
    get() = impl().typeArgumentsAsTypes.map { it.model() }
}
