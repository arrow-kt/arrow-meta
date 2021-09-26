package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ObjectDeclaration
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.ObjectLiteralExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtObjectLiteralExpression

fun interface KotlinObjectLiteralExpression : ObjectLiteralExpression, KotlinExpression {
  override fun impl(): KtObjectLiteralExpression
  override val objectDeclaration: ObjectDeclaration
    get() = impl().objectDeclaration.model()
}
