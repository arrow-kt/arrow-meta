@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.java.ast.modelCautious
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BlockExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DeclarationWithBody
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FunctionLiteral
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.LambdaExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Parameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ParameterList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeConstraint
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeConstraintList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeParameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeParameterList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import com.sun.source.tree.LambdaExpressionTree

public class JavaLambda(private val ctx: AnalysisContext, private val impl: LambdaExpressionTree) :
  LambdaExpression, FunctionLiteral, DeclarationWithBody, JavaElement(ctx, impl) {
  override val functionLiteral: FunctionLiteral
    get() = this

  override val name: String? = null
  override val nameAsSafeName: Name = Name("<lambda>")
  override val fqName: FqName? = null
  override val nameAsName: Name? = null

  override val parents: List<Element>
    get() = ctx.resolver.parentTrees(impl).mapNotNull { it.modelCautious(ctx) }

  override val valueParameters: List<Parameter>
    get() = impl.parameters.map { JavaParameter(ctx, it, this) }
  override val valueParameterList: ParameterList
    get() =
      object : ParameterList {
        override val parameters: List<Parameter>
          get() = this@JavaLambda.valueParameters
        override val ownerFunction: DeclarationWithBody
          get() = this@JavaLambda
      }

  override fun hasParameterSpecification(): Boolean = false

  override val receiverTypeReference: TypeReference?
    get() = null
  override val typeReference: TypeReference?
    get() = null

  override val typeParameters: List<TypeParameter>
    get() = emptyList()
  override val typeParameterList: TypeParameterList
    get() =
      object : TypeParameterList {
        override val parameters: List<TypeParameter>
          get() = emptyList()
      }

  override val typeConstraints: List<TypeConstraint>
    get() = emptyList()
  override val typeConstraintList: TypeConstraintList
    get() =
      object : TypeConstraintList {
        override val constraints: List<TypeConstraint>
          get() = emptyList()
      }

  override fun hasBody(): Boolean = impl.body != null
  override fun body(): Expression = impl.body.model(ctx)
  override val bodyExpression: Expression
    get() = body()
  override val bodyBlockExpression: BlockExpression?
    get() = body() as? BlockExpression
  override fun hasBlockBody(): Boolean = bodyBlockExpression != null

  override val isLocal: Boolean = true

  // Java lambda's cannot do this
  override fun hasDeclaredReturnType(): Boolean = false
}
