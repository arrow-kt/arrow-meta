package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ObjectDeclaration
import org.jetbrains.kotlin.psi.KtObjectDeclaration

fun interface KotlinObjectDeclaration : ObjectDeclaration, KotlinClassOrObject {
  override fun impl(): KtObjectDeclaration
  override fun isCompanion(): Boolean =
    impl().isCompanion()

  override fun isObjectLiteral(): Boolean =
    impl().isObjectLiteral()
}
