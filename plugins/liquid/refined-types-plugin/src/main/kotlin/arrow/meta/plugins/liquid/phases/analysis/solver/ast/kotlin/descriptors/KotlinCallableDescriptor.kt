package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.Type
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.Annotations
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ModuleDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ReceiverParameterDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.TypeParameterDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.types.KotlinType

fun interface KotlinCallableDescriptor :
  CallableDescriptor,
  DeclarationDescriptor {

  fun impl(): org.jetbrains.kotlin.descriptors.CallableDescriptor

  fun descriptor(): KotlinDeclarationDescriptor = KotlinDeclarationDescriptor ({ impl() })

  override val annotations: Annotations
    get() = KotlinAnnotations { descriptor().impl().annotations }
  override val module: ModuleDescriptor
    get() = TODO("Not yet implemented")
  override val containingDeclaration: DeclarationDescriptor?
    get() = TODO("Not yet implemented")

  override fun element(): Element? {
    TODO("Not yet implemented")
  }

  override val fqNameSafe: FqName
    get() = TODO("Not yet implemented")
  override val name: Name
    get() = TODO("Not yet implemented")
  override val extensionReceiverParameter: ReceiverParameterDescriptor?
    get() = impl().extensionReceiverParameter?.model()
  override val dispatchReceiverParameter: ReceiverParameterDescriptor?
    get() = impl().dispatchReceiverParameter?.model()
  override val typeParameters: List<TypeParameterDescriptor>
    get() = impl().typeParameters.map { it.model() }
  override val returnType: Type?
    get() = impl().returnType?.let { KotlinType(it) }
  override val valueParameters: List<ValueParameterDescriptor>
    get() = impl().valueParameters.map { it.model() }
  override val overriddenDescriptors: Collection<CallableDescriptor>
    get() = impl().overriddenDescriptors.map { it.model() }
}
