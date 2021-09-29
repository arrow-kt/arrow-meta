
package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.AnnotationsContainer
import org.jetbrains.kotlin.psi.KtAnnotationsContainer

fun interface KotlinAnnotationsContainer : AnnotationsContainer, KotlinElement {
  override fun impl(): KtAnnotationsContainer
}
