package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

import arrow.meta.phases.analysis.body
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.BlockExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.DeclarationWithBody
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Parameter
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDeclarationWithBody

fun interface KotlinDeclarationWithBody : DeclarationWithBody, KotlinDeclaration {
  override fun impl(): KtDeclarationWithBody
  override val bodyExpression: Expression?
    get() = impl().bodyExpression?.model()

  override fun hasBlockBody(): Boolean =
    impl().hasBlockBody()

  override fun hasBody(): Boolean =
    impl().hasBody()

  override fun hasDeclaredReturnType(): Boolean =
    impl().hasDeclaredReturnType()

  override fun body(): Expression? =
    impl().body()?.model()

  override val valueParameters: List<Parameter?>
    get() = impl().valueParameters.map { it.model() }

  override val bodyBlockExpression: BlockExpression?
    get() = impl().bodyBlockExpression?.model()
}
