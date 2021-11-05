@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.java.ast.modelCautious
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CallableDeclaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.DestructuringDeclaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Parameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ParameterList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeConstraint
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeConstraintList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeParameter
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeParameterList
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.VariableDeclaration
import com.sun.source.tree.VariableTree

public open class JavaParameterOrVariable(ctx: AnalysisContext, impl: VariableTree) :
  CallableDeclaration, JavaElement(ctx, impl) {

  override val parents: List<Element> =
    ctx.resolver.parentTrees(impl).mapNotNull { it.modelCautious(ctx) }
  final override val name: String = impl.name.toString()
  final override val nameAsSafeName: Name = Name(name)
  override val fqName: FqName = FqName(impl.fqName(ctx))
  override val nameAsName: Name = nameAsSafeName

  override val typeReference: TypeReference? = impl.type.model(ctx)
  override val receiverTypeReference: TypeReference? = null
  override val valueParameters: List<Parameter> = emptyList()
  override val valueParameterList: ParameterList? = null
  override val typeParameters: List<TypeParameter> = emptyList()
  override val typeParameterList: TypeParameterList? = null
  override val typeConstraints: List<TypeConstraint> = emptyList()
  override val typeConstraintList: TypeConstraintList? = null
}

public class JavaVariable(ctx: AnalysisContext, private val impl: VariableTree) :
  VariableDeclaration, JavaParameterOrVariable(ctx, impl) {
  override val isVar: Boolean = true
  override val initializer: Expression? = impl.initializer?.model(ctx)
  override fun hasInitializer(): Boolean = impl.initializer != null
}

public class JavaParameter(
  ctx: AnalysisContext,
  private val impl: VariableTree,
  public override val ownerFunction: JavaMethod
) : Parameter, JavaParameterOrVariable(ctx, impl) {
  override fun hasDefaultValue(): Boolean = impl.initializer != null
  override val defaultValue: Expression? = impl.initializer?.model(ctx)

  override val isMutable: Boolean = false
  override val isVarArg: Boolean = false
  override fun hasValOrVar(): Boolean = false
  override val isLoopParameter: Boolean = false
  override val isCatchParameter: Boolean = false

  override val destructuringDeclaration: DestructuringDeclaration? = null
}
