@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ReceiverValue
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ResolvedValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import com.sun.source.tree.Tree
import com.sun.tools.javac.code.Symbol
import com.sun.tools.javac.tree.JCTree

public class JavaResolvedCall(
  private val ctx: AnalysisContext,
  private val whole: Tree,
  private val method: Symbol,
  private val receiver: Tree?,
  private val typeArgs: List<Tree>,
  private val arguments: List<Tree>
) : ResolvedCall {

  override val callElement: Element
    get() = whole.model(ctx)

  override fun getReceiverExpression(): JavaElement? = receiver?.model(ctx)
  override val dispatchReceiver: ReceiverValue?
    get() =
      getReceiverExpression()?.let {
        object : ReceiverValue {
          override val type: Type = it.type()!!
        }
      }
  // there are no extension receivers in Java
  override val extensionReceiver: ReceiverValue? = null

  override val resultingDescriptor: CallableDescriptor
    get() = method.model(ctx)
  override fun getReturnType(): Type = whole.model<Tree, JavaElement>(ctx).type()!!

  override val typeArguments: Map<TypeParameterDescriptor, Type>
    get() =
      resultingDescriptor
        .typeParameters
        .zip(typeArgs)
        .map { (descr, tree) -> descr to ctx.resolver.resolveType(tree)!!.model(ctx) }
        .toMap()
  override val valueArguments: Map<ValueParameterDescriptor, ResolvedValueArgument>
    get() =
      resultingDescriptor
        .valueParameters
        .zip(arguments)
        .map { (descr, tree) ->
          descr to
            when (tree) {
              null -> JavaDefaultValueArgument(descr)
              else -> JavaExpressionValueArgument(ctx, tree, descr)
            }
        }
        .toMap()
}

public fun Tree.resolvedCall(ctx: AnalysisContext): JavaResolvedCall? =
  when (this) {
    is JCTree.JCOperatorExpression ->
      JavaResolvedCall(ctx, this, operator, null, emptyList(), argumentsFromEverywhere)
    is JCTree.JCMethodInvocation ->
      when (val m = this.meth) {
        is JCTree.JCMemberReference ->
          m.sym?.let { sym ->
            JavaResolvedCall(
              ctx,
              this,
              sym,
              m.qualifierExpression,
              typeArguments + m.typeArguments,
              arguments
            )
          }
        is JCTree.JCIdent ->
          m.sym?.let { sym -> JavaResolvedCall(ctx, this, sym, null, typeArguments, arguments) }
        is JCTree.JCFieldAccess ->
          m.sym?.let { sym -> JavaResolvedCall(ctx, this, sym, null, typeArguments, arguments) }
        else -> null
      }
    is JCTree.JCNewClass ->
      constructor?.let { sym -> JavaResolvedCall(ctx, this, sym, null, typeArguments, arguments) }
    is JCTree.JCIdent ->
      sym?.let { sym -> JavaResolvedCall(ctx, this, sym, null, emptyList(), emptyList()) }
    else -> null
  }
