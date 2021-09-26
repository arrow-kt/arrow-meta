package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.Annotations
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.CallableDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.CallableMemberDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.MemberDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.ModuleDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.ast.model
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor.Kind.*
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module

fun interface KotlinCallableMemberDescriptor :
  CallableMemberDescriptor,
  KotlinCallableDescriptor,
  KotlinMemberDescriptor {

  override fun impl(): org.jetbrains.kotlin.descriptors.CallableMemberDescriptor

  override val kind: CallableMemberDescriptor.Kind
    get() = when (impl().kind) {
      DECLARATION -> CallableMemberDescriptor.Kind.DECLARATION
      FAKE_OVERRIDE -> CallableMemberDescriptor.Kind.FAKE_OVERRIDE
      DELEGATION -> CallableMemberDescriptor.Kind.DELEGATION
      SYNTHESIZED -> CallableMemberDescriptor.Kind.SYNTHESIZED
    }

  override fun annotations(): Annotations = KotlinAnnotations (impl().annotations)
  override val module: ModuleDescriptor
    get() = KotlinModuleDescriptor { impl().module }
  override val containingDeclaration: DeclarationDescriptor?
    get() = impl().containingDeclaration.model()

  override fun element(): Element? =
    (impl().findPsi() as? KtElement)?.model()

  override val fqNameSafe: FqName
    get() = FqName(impl().fqNameSafe.asString())
  override val name: Name
    get() = Name(impl().name.asString())
}
