package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ClassLiteralExpression
import org.jetbrains.kotlin.psi.KtClassLiteralExpression

interface KotlinClassLiteralExpression : ClassLiteralExpression, KotlinDoubleColonExpression {
  override fun impl(): KtClassLiteralExpression
}
