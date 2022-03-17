package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.AnonymousInitializer
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Declaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtAnonymousInitializer

fun interface KotlinAnonymousInitializer : AnonymousInitializer, KotlinDeclaration {
  override fun impl(): KtAnonymousInitializer
  override val containingDeclaration: Declaration
    get() = impl().containingDeclaration.model()
  override val body: Expression?
    get() = impl().body?.model()
}
