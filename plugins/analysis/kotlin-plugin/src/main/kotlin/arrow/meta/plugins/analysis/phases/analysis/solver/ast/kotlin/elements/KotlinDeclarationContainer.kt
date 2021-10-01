
package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Declaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DeclarationContainer
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtDeclarationContainer

fun interface KotlinDeclarationContainer : DeclarationContainer {
  fun impl(): KtDeclarationContainer
  override val declarations: List<Declaration>
    get() = impl().declarations.map { it.model() }
}
