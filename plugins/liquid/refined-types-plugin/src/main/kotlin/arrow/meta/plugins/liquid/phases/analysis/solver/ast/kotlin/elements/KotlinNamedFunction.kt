package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.NamedFunction
import org.jetbrains.kotlin.psi.KtNamedFunction

class KotlinNamedFunction(val impl: KtNamedFunction) : NamedFunction, KotlinFunction, KotlinDeclarationWithInitializer {
  override fun impl(): KtNamedFunction = impl
}
