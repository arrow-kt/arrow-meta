package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeAlias
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtTypeAlias

fun interface KotlinTypeAlias : TypeAlias, KotlinNamedDeclaration {
  override fun impl(): KtTypeAlias
  override fun isTopLevel(): Boolean =
    impl().isTopLevel()

  override fun getTypeReference(): TypeReference? =
    impl().getTypeReference()?.model()
}
