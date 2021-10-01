package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

fun interface KotlinSimpleNameExpression : SimpleNameExpression, KotlinReferenceExpression {
  override fun impl(): KtSimpleNameExpression
  override fun getReferencedName(): String =
    impl().getReferencedName()

  override fun getReferencedNameAsName(): Name =
    Name(impl().getReferencedNameAsName().asString())
}
