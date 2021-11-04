@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.MemberDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.Modality
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.Visibility
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier

public open class JavaMemberDescriptor(ctx: AnalysisContext, impl: Element) :
  MemberDescriptor, JavaDescriptor(ctx, impl) {

  override val modality: Modality =
    when {
      impl.modifiers.contains(Modifier.FINAL) -> Modality.FINAL
      impl.modifiers.contains(Modifier.ABSTRACT) || impl.kind == ElementKind.INTERFACE ->
        Modality.ABSTRACT
      else -> Modality.OPEN
    }

  override val visibility: Visibility =
    object : Visibility {
      override val isPublicAPI: Boolean = impl.modifiers.contains(Modifier.PUBLIC)
      // this is the same as the simple name
      override val name: String = impl.simpleName.toString()
    }

  override val isExpect: Boolean = false
  override val isActual: Boolean = false
  override val isExternal: Boolean = false
}
