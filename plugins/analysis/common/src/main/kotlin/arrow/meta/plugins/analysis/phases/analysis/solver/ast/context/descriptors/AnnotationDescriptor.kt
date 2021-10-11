package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Name
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type

interface AnnotationDescriptor {
  val allValueArguments: Map<Name, Any?>

  fun argumentValueAsString(argName: String): String?
  fun argumentValueAsArrayOfString(argName: String): List<String>

  val fqName: FqName?

  val type: Type
}
