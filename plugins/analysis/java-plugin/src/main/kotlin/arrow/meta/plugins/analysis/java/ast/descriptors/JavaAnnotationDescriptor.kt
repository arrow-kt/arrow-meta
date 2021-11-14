@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.descriptors

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.AnnotationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue

public class JavaAnnotationDescriptor(
  private val ctx: AnalysisContext,
  private val impl: AnnotationMirror
) : AnnotationDescriptor {
  private val inside: Map<String, AnnotationValue>
    get() =
      ctx
        .elements
        .getElementValuesWithDefaults(impl)
        .map { (k, v) -> k.simpleName.toString() to v }
        .toMap()

  override val allValueArguments: Map<Name, Any?>
    get() = inside.mapKeys { (k, _) -> Name(k) }.mapValues { (_, v) -> v.value }

  override fun argumentValueAsString(argName: String): String? =
    inside.get(argName)?.value as? String

  override fun argumentValueAsArrayOfString(argName: String): List<String> =
    (inside.get(argName)?.value as? List<*>)
      ?.filterIsInstance<AnnotationValue>()
      ?.mapNotNull { it.value as? String }
      .orEmpty()

  override val fqName: FqName
    get() = FqName(impl.annotationType.asElement().fqName)
  override val type: Type
    get() = impl.annotationType.model(ctx)
}
