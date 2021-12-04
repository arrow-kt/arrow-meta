@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.descriptors

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.Annotations
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ReceiverParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ReceiverValue
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import javax.lang.model.type.TypeMirror

public class JavaReceiverParameterDescriptor(
  private val ctx: AnalysisContext,
  private val ty: TypeMirror,
  private val enclosing: javax.lang.model.element.Element
) : ReceiverParameterDescriptor {
  override fun impl(): TypeMirror = ty

  override val name: Name = Name("this")
  override val fqNameSafe: FqName = FqName("this")

  override val type: Type
    get() = ty.model(ctx)
  override val value: ReceiverValue
    get() =
      object : ReceiverValue {
        override val type: Type = this@JavaReceiverParameterDescriptor.type
        override val isClassReceiver: Boolean
          get() = false // TODO check this later
      }
  override val allParameters: List<ParameterDescriptor> = emptyList()
  override val extensionReceiverParameter: ReceiverParameterDescriptor? = null
  override val dispatchReceiverParameter: ReceiverParameterDescriptor? = null
  override val typeParameters: List<TypeParameterDescriptor> = emptyList()
  override val returnType: Type
    get() = type
  override val valueParameters: List<ValueParameterDescriptor> = emptyList()
  override val overriddenDescriptors: Collection<CallableDescriptor> = emptyList()

  override val containingDeclaration: DeclarationDescriptor
    get() = enclosing.model(ctx)

  override val containingPackage: FqName?
    get() = containingDeclaration.containingPackage

  override fun element(): Element? = null
  override fun annotations(): Annotations = JavaAnnotations(ctx, emptyList())
}
