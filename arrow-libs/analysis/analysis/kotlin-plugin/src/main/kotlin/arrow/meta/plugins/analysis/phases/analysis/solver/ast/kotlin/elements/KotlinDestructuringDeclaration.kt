package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DestructuringDeclaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DestructuringDeclarationEntry
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtDestructuringDeclaration

class KotlinDestructuringDeclaration(val impl: KtDestructuringDeclaration) :
  DestructuringDeclaration, KotlinDeclarationWithInitializer {
  override fun impl(): KtDestructuringDeclaration = impl
  override val entries: List<DestructuringDeclarationEntry>
    get() = impl().entries.map { it.model() }
  override val isVar: Boolean
    get() = impl().isVar
}
