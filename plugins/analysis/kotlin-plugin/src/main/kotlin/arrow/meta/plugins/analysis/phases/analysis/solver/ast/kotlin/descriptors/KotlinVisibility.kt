package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.Visibility

fun interface KotlinVisibility : Visibility {
  fun impl(): org.jetbrains.kotlin.descriptors.Visibility
  override val isPublicAPI: Boolean
    get() = impl().isPublicAPI
  override val name: String
    get() = impl().name
}
