package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.Visibility

internal class KotlinVisibility(private val impl: org.jetbrains.kotlin.descriptors.Visibility): Visibility {
  override val isPublicAPI: Boolean
    get() = impl.isPublicAPI
  override val name: String
    get() = impl.name
}
