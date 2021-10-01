package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import org.jetbrains.kotlin.psi.KtElement

open class KotlinDefaultElement(open val impl: KtElement) : KotlinElement {
  override fun impl(): KtElement = impl
}
