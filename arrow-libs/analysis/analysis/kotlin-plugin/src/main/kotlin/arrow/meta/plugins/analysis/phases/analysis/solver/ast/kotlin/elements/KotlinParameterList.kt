package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtParameterList

class KotlinParameterList(val impl: KtParameterList) : KotlinElement {
  override fun impl(): KtElement = impl
}
