@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.descriptors.enclosingClass
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.java.ast.modelCautious
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BlockExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ClassOrObject
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ConstructorDelegationCall
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DeclarationWithBody
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.NamedFunction
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Parameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ParameterList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SecondaryConstructor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeConstraint
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeConstraintList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeParameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeParameterList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import com.sun.source.tree.MethodTree
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement

public open class JavaMethod(private val ctx: AnalysisContext, private val impl: MethodTree) :
  NamedFunction, JavaElement(ctx, impl) {

  override val parents: List<Element>
    get() = ctx.resolver.parentTrees(impl).mapNotNull { it.modelCautious(ctx) }
  override val name: String
    get() = impl.name.toString()
  override val nameAsSafeName: Name
    get() = Name(name)
  override val fqName: FqName
    get() = FqName(impl.fqName(ctx))
  override val nameAsName: Name
    get() = nameAsSafeName

  override val isLocal: Boolean
    get() = ctx.resolver.parent(impl) is MethodTree

  override val bodyExpression: Expression?
    get() = impl.body?.model(ctx)
  override fun hasBody(): Boolean = bodyExpression != null
  override fun body(): Expression? = bodyExpression

  override fun hasInitializer(): Boolean = bodyExpression != null
  override val initializer: Expression?
    get() = bodyExpression

  override val bodyBlockExpression: BlockExpression?
    get() = bodyExpression as? JavaBlock
  override fun hasBlockBody(): Boolean = bodyBlockExpression != null

  override fun hasDeclaredReturnType(): Boolean = impl.returnType != null
  override val typeReference: TypeReference?
    get() = impl.returnType?.let { JavaTypeReference(ctx, it) }
  override val receiverTypeReference: TypeReference?
    get() = impl.receiverParameter?.type?.let { JavaTypeReference(ctx, it) }

  override val typeParameters: List<TypeParameter>
    get() = impl.typeParameters.map { JavaTypeParameter(ctx, it) }
  override val typeParameterList: TypeParameterList
    get() =
      object : TypeParameterList {
        override val parameters: List<TypeParameter> = this@JavaMethod.typeParameters
      }

  override val valueParameters: List<Parameter>
    get() = impl.parameters.map { JavaParameter(ctx, it, this) }
  override val valueParameterList: ParameterList
    get() =
      object : ParameterList {
        override val parameters: List<Parameter> = this@JavaMethod.valueParameters
        override val ownerFunction: DeclarationWithBody = this@JavaMethod
      }

  // not available in Java
  override val typeConstraints: List<TypeConstraint> = emptyList()
  override val typeConstraintList: TypeConstraintList? = null
}

public class JavaConstructor(private val ctx: AnalysisContext, impl: MethodTree) :
  SecondaryConstructor, JavaMethod(ctx, impl) {

  private val element: ExecutableElement = ctx.resolver.resolve(impl) as ExecutableElement

  init {
    require(element.kind == ElementKind.CONSTRUCTOR)
  }

  // yep, very unsafe, but we assume everything works smoothly
  override fun getContainingClassOrObject(): ClassOrObject =
    ctx.resolver.tree(element.enclosingClass!!)!!.model(ctx)

  override fun getDelegationCall(): ConstructorDelegationCall? = null
}
