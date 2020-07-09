package arrow.meta.plugins.patternMatching.phases.analysis

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor.Kind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.PropertyDescriptorImpl
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.resolve.BindingContext.DECLARATION_TO_DESCRIPTOR
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.source.toSourceElement
import org.jetbrains.kotlin.types.KotlinType

class PlaceholderPropertyDescriptor(
  val parameterIndex: Int,
  target: KtExpression,
  bindingTrace: BindingTrace,
  type: KotlinType
) : PropertyDescriptorImpl(
  target.findContainingDeclaration(bindingTrace)!!,
  null,
  Annotations.EMPTY,
  Modality.FINAL,
  Visibilities.DEFAULT_VISIBILITY,
  false,
  Name.identifier("param$parameterIndex"),
  Kind.SYNTHESIZED,
  target.toSourceElement(),
  false,
  false,
  false,
  false,
  false,
  false
) {
  init {
    setType(type, emptyList(), null, null)
    initialize(null, null)
  }

  companion object {
    private fun KtExpression.findContainingDeclaration(bindingTrace: BindingTrace) =
      getParentOfType<KtDeclaration>(strict = true)?.let { bindingTrace[DECLARATION_TO_DESCRIPTOR, it] }
  }
}
