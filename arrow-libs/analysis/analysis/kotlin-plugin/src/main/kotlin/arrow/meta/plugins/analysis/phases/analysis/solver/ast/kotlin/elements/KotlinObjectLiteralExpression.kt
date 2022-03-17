package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ObjectDeclaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ObjectLiteralExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtObjectLiteralExpression

class KotlinObjectLiteralExpression(val impl: KtObjectLiteralExpression) :
  ObjectLiteralExpression, KotlinExpression {
  override fun impl(): KtObjectLiteralExpression = impl
  override val objectDeclaration: ObjectDeclaration
    get() = impl().objectDeclaration.model()
}
