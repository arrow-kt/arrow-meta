package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Function
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Parameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtFunction

interface KotlinFunction : Function, KotlinDeclarationWithBody, KotlinCallableDeclaration {
  override fun impl(): KtFunction
  override val isLocal: Boolean
    get() = impl().isLocal
  override val valueParameters: List<Parameter>
    get() = impl().valueParameters.map { it.model() }
}

class KotlinDefaultFunction(val impl: KtFunction) : KotlinFunction {
  override fun impl(): KtFunction = impl
}
