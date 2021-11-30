@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.descriptors

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.java.ast.types.JavaType
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableMemberDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.FieldDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.PropertyAccessorDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.PropertyDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ReceiverParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.VariableAccessorDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import com.sun.tools.javac.code.Symbol
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.VariableElement

public class JavaFieldDescriptor(
  private val ctx: AnalysisContext,
  private val impl: VariableElement
) : PropertyDescriptor, JavaMemberDescriptor(ctx, impl) {
  init {
    require(impl.kind == ElementKind.FIELD)
  }

  override val isVar: Boolean = !impl.modifiers.contains(Modifier.FINAL)
  override val isConst: Boolean = impl.constantValue != null

  override val type: JavaType
    get() =
      when (impl) {
        is Symbol -> impl.type.model(ctx)
        else -> throw IllegalStateException("this element should be a symbol")
      }

  // fields in Java never override those in parent classes
  override val overriddenDescriptors: Collection<CallableDescriptor> = emptyList()

  override val isSetterProjectedOut: Boolean = false
  override val accessors: List<PropertyAccessorDescriptor> = emptyList()
  override val backingField: FieldDescriptor? = null
  override val delegateField: FieldDescriptor? = null
  override val getter: VariableAccessorDescriptor? = null
  override val isDelegated: Boolean = false
  override val setter: VariableAccessorDescriptor? = null
  override val isLateInit: Boolean = false
  override val kind: CallableMemberDescriptor.Kind = CallableMemberDescriptor.Kind.DECLARATION

  override val allParameters: List<ParameterDescriptor> = emptyList()
  override val extensionReceiverParameter: ReceiverParameterDescriptor? = null
  override val dispatchReceiverParameter: ReceiverParameterDescriptor?
    get() =
      if (impl.modifiers.contains(Modifier.STATIC)) null
      else JavaReceiverParameterDescriptor(ctx, impl.enclosingClass!!.asType(), impl)
  override val typeParameters: List<TypeParameterDescriptor> = emptyList()
  override val returnType: Type = type
  override val valueParameters: List<ValueParameterDescriptor> = emptyList()
}
