package arrow.meta.plugins.analysis.smt.utils

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ResolvedValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

data class ReferencedElement(
  val element: Element,
  val reference: Pair<ValueParameterDescriptor, ResolvedValueArgument>?,
  val type: Type?
)

class NameProvider {
  private val counter = AtomicReference(0)

  private val assignedNames: ConcurrentHashMap<String, ReferencedElement?> = ConcurrentHashMap()

  fun mirroredElement(assignedName: String): ReferencedElement? =
    assignedNames[assignedName]

  fun recordNewName(prefix: String, mirroredElement: ReferencedElement?): String {
    val n = counter.getAndUpdate { it + 1 }
    val newName = "${prefix}$n"
    if (mirroredElement != null)
      assignedNames[newName] = mirroredElement
    return newName
  }
}
