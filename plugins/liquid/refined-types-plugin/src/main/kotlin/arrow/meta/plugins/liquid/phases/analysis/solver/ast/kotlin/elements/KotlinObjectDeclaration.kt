package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ObjectDeclaration
import org.jetbrains.kotlin.psi.KtObjectDeclaration

class KotlinObjectDeclaration(val impl: KtObjectDeclaration) : ObjectDeclaration, KotlinClassOrObject {
  override fun impl(): KtObjectDeclaration = impl

  override fun isCompanion(): Boolean =
    impl().isCompanion()

  override fun isObjectLiteral(): Boolean =
    impl().isObjectLiteral()
}
