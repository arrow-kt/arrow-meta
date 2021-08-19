package arrow.meta.plugins.liquid.smt.utils

import java.util.concurrent.atomic.AtomicReference

class NameProvider {
  private val counter = AtomicReference(0)

  fun newName(prefix: String): String {
    val n = counter.getAndUpdate { it + 1 }
    return "${prefix}$n"
  }
}
