package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.Named
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name

fun interface KotlinNamed : Named {
  fun impl(): org.jetbrains.kotlin.descriptors.Named
  override val name: Name
    get() = Name(impl().name.asString())
}
