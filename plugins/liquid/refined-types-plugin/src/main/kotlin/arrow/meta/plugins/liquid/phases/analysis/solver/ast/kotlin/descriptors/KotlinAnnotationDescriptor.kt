package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.Type
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors.AnnotationDescriptor
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.types.KotlinType
import org.jetbrains.kotlin.resolve.annotations.argumentValue

fun interface KotlinAnnotationDescriptor :
  AnnotationDescriptor {

  fun impl(): org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor

  override val allValueArguments: Map<Name, Any?>
    get() =
      impl().allValueArguments.map { (key, value) -> Name(key.asString()) to value.value }.toMap()

  override fun argumentValueAsString(argName: String): String? =
    impl().argumentValue(argName)?.value as? String

  override fun argumentValueAsArrayOfString(argName: String): List<String> =
    (impl().argumentValue(argName)?.value as? Array<*>)?.filterIsInstance<String>().orEmpty()

  override val fqName: FqName?
    get() = impl().fqName?.let { FqName(it.asString()) }

  override val type: Type
    get() = KotlinType(impl().type)
}
