package arrow.meta.plugins.liquid.smt.utils

import org.jetbrains.kotlin.psi.KtElement
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

class NameProvider {
  private val counter = AtomicReference(0)

  private val assignedNames: ConcurrentHashMap<String, KtElement?> = ConcurrentHashMap()

  fun mirroredElement(assignedName: String): KtElement? =
    assignedNames[assignedName]

  fun newName(prefix: String, mirroredElement: KtElement?): String {
    val n = counter.getAndUpdate { it + 1 }
    val newName = "${prefix}$n"
    if (mirroredElement != null)
      assignedNames[newName] = mirroredElement
    return newName
  }
}
