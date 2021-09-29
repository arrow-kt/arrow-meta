package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.Type

interface AnnotationDescriptor {
  val allValueArguments: Map<Name, Any?>

  fun argumentValueAsString(argName: String): String?
  fun argumentValueAsArrayOfString(argName: String): List<String>

  val fqName: FqName?

  val type: Type
}
