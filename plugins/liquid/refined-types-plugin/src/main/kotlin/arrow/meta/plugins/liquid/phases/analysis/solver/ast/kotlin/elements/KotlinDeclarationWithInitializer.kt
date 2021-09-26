package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.DeclarationWithInitializer
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtDeclarationWithInitializer

fun interface KotlinDeclarationWithInitializer : DeclarationWithInitializer, KotlinDeclaration {
  override fun impl(): KtDeclarationWithInitializer

  override val initializer: Expression?
    get() = impl().initializer?.model()

  override fun hasInitializer(): Boolean =
    impl().hasInitializer()
}
