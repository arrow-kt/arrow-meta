package arrow.meta.plugins.liquid.smt.utils

import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.calls.model.ResolvedValueArgument
import org.jetbrains.kotlin.types.KotlinType
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

data class ReferencedElement(
  val element: KtElement,
  val reference: Pair<ValueParameterDescriptor, ResolvedValueArgument>?,
  val type: KotlinType?
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
