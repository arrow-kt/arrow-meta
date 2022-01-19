package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.NamedFunction
import org.jetbrains.kotlin.psi.KtNamedFunction

class KotlinNamedFunction(val impl: KtNamedFunction) :
  KotlinFunction, NamedFunction, KotlinDeclarationWithInitializer {
  override fun impl(): KtNamedFunction = impl
}
