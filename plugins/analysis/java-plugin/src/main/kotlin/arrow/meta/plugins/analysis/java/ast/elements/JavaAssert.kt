@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.descriptors.JavaAnnotations
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.Annotations
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ReceiverParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ReceiverValue
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ResolvedValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.AssertExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import com.sun.source.tree.AssertTree
import javax.lang.model.type.TypeMirror

public class JavaAssert(private val ctx: AnalysisContext, private val impl: AssertTree) :
  AssertExpression, JavaElement(ctx, impl) {
  override fun getResolvedCall(context: ResolutionContext): ResolvedCall =
    JavaAssertFakeCall(ctx, impl)
  override val condition: Expression
    get() = impl.condition.model(ctx)
  override val detail: Expression?
    get() = impl.detail?.model(ctx)
}

public class JavaAssertFakeCall(private val ctx: AnalysisContext, private val impl: AssertTree) :
  ResolvedCall {
  override val callElement: Element
    get() = impl.model(ctx)
  override val typeArguments: Map<TypeParameterDescriptor, Type>
    get() = emptyMap()

  private val fakeDescriptor: JavaAssertFakeDescriptor =
    JavaAssertFakeDescriptor(ctx, impl.detail != null)
  override val resultingDescriptor: CallableDescriptor
    get() = fakeDescriptor

  override val valueArguments: Map<ValueParameterDescriptor, ResolvedValueArgument>
    get() =
      if (impl.detail != null) {
        mapOf(
          fakeDescriptor.predicateParameter to
            JavaExpressionValueArgument(ctx, impl.condition, fakeDescriptor.predicateParameter),
          fakeDescriptor.messageParameter to
            JavaExpressionValueArgument(ctx, impl.detail, fakeDescriptor.messageParameter)
        )
      } else {
        mapOf(
          fakeDescriptor.predicateParameter to
            JavaExpressionValueArgument(ctx, impl.condition, fakeDescriptor.predicateParameter)
        )
      }

  override fun getReturnType(): Type = ctx.symbolTable.voidType.model(ctx)

  override fun getReceiverExpression(): Expression? = null
  override val dispatchReceiver: ReceiverValue? = null
  override val extensionReceiver: ReceiverValue? = null
}

public class JavaAssertFakeDescriptor(
  private val ctx: AnalysisContext,
  private val withDetail: Boolean
) : CallableDescriptor {
  override fun impl(): Any = this

  override val fqNameSafe: FqName = FqName(AssertExpression.FAKE_ASSERT_NAME)
  override val name: Name = Name(AssertExpression.FAKE_ASSERT_NAME)

  override val extensionReceiverParameter: ReceiverParameterDescriptor? = null
  override val dispatchReceiverParameter: ReceiverParameterDescriptor? = null
  override val typeParameters: List<TypeParameterDescriptor> = emptyList()
  override val returnType: Type = ctx.symbolTable.voidType.model(ctx)

  public val predicateParameter: ValueParameterDescriptor =
    JavaAssertFakeParameter(ctx, 0, "predicate", ctx.symbolTable.booleanType, this)
  public val messageParameter: ValueParameterDescriptor =
    JavaAssertFakeParameter(ctx, 1, "msg", ctx.symbolTable.stringType, this)

  override val valueParameters: List<ValueParameterDescriptor> =
    if (withDetail) listOf(predicateParameter, messageParameter) else listOf(predicateParameter)
  override val allParameters: List<ParameterDescriptor> = valueParameters

  override fun annotations(): Annotations = JavaAnnotations(ctx, emptyList())
  override val overriddenDescriptors: Collection<CallableDescriptor> = emptyList()
  override val containingDeclaration: DeclarationDescriptor? = null
  override val containingPackage: FqName? = null
  override fun element(): Element? = null
}

public class JavaAssertFakeParameter(
  private val ctx: AnalysisContext,
  public override val index: Int,
  name: String,
  private val typeMirror: TypeMirror,
  override val containingDeclaration: DeclarationDescriptor
) : ValueParameterDescriptor {
  override fun impl(): Any = this

  override val type: Type
    get() = typeMirror.model(ctx)
  override val returnType: Type
    get() = type
  override val fqNameSafe: FqName = FqName(name)
  override val name: Name = Name(name)

  override val isCrossinline: Boolean = false
  override val isNoinline: Boolean = false
  override val varargElementType: Type? = null
  override fun declaresDefaultValue(): Boolean = false
  override val defaultValue: Expression? = null
  override val isVar: Boolean = false
  override val isConst: Boolean = false
  override val isLateInit: Boolean = false

  override val allParameters: List<ParameterDescriptor> = emptyList()
  override val extensionReceiverParameter: ReceiverParameterDescriptor? = null
  override val dispatchReceiverParameter: ReceiverParameterDescriptor? = null
  override val typeParameters: List<TypeParameterDescriptor> = emptyList()
  override val valueParameters: List<ValueParameterDescriptor> = emptyList()
  override val overriddenDescriptors: Collection<CallableDescriptor> = emptyList()

  override val containingPackage: FqName? = null
  override fun element(): Element? = null
  override fun annotations(): Annotations = JavaAnnotations(ctx, emptyList())
}
