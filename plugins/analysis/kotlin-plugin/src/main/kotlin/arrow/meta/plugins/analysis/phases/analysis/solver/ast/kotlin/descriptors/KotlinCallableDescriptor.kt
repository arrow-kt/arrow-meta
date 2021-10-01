package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.Annotations
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ModuleDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ReceiverParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.types.KotlinType
import org.jetbrains.kotlin.backend.common.descriptors.allParameters
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module

fun interface KotlinCallableDescriptor :
  CallableDescriptor {

  fun impl(): org.jetbrains.kotlin.descriptors.CallableDescriptor

  fun descriptor(): KotlinDeclarationDescriptor = KotlinDeclarationDescriptor({ impl() })

  override fun annotations(): Annotations = KotlinAnnotations(descriptor().impl().annotations)
  override val module: ModuleDescriptor
    get() = impl().module.model()
  override val containingDeclaration: DeclarationDescriptor?
    get() = impl().containingDeclaration.model()

  override fun element(): Element? =
    (impl().findPsi() as? KtElement)?.model()

  override val fqNameSafe: FqName
    get() = FqName(impl().fqNameSafe.asString())
  override val name: Name
    get() = Name(impl().name.asString())
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

  override val allParameters: List<ParameterDescriptor>
    get() = impl().allParameters.map { it.model() }
}
