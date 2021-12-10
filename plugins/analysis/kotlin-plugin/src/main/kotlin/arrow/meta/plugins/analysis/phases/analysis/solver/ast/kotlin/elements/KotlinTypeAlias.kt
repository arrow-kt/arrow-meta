package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeAlias
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtTypeAlias

class KotlinTypeAlias(val impl: KtTypeAlias) : TypeAlias, KotlinNamedDeclaration {
  override fun impl(): KtTypeAlias = impl
  override fun isTopLevel(): Boolean = impl.isTopLevel()
  override fun getTypeReference(): TypeReference? = impl.getTypeReference()?.model()
}
