package arrow.meta.plugins.liquid.smt.utils

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

class NameProvider {
  private val counter = AtomicReference(0)

  private val assignedNames: ConcurrentHashMap<String, String> = ConcurrentHashMap()

  fun kotlinName(assignedName: String): String =
    assignedNames[assignedName] ?: assignedName

  fun newName(prefix: String): String {
    val n = counter.getAndUpdate { it + 1 }
    val newName = "${prefix}$n"
    assignedNames[newName] = prefix
    return newName
  }
}
