package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BlockExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FinallySection
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtFinallySection

fun interface KotlinFinallySection : FinallySection, KotlinElement {
  override fun impl(): KtFinallySection
  override val finalExpression: BlockExpression
    get() = impl().finalExpression.model()
}
