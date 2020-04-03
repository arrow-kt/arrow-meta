package arrow.meta.ide.testing

import arrow.meta.ide.phases.resolve.LOG

fun unavailableServices(vararg service: Class<*>): Unit =
  LOG.error("Unavailable Service/s: ${service.joinToString(separator = ", ") { it.name }}")

fun unavailable(vararg service: Class<*>): UnavailableServices =
  UnavailableServices(service.toList())

data class UnavailableServices(val service: List<Class<*>>) :
  RuntimeException("Unavailable Service/s: ${service.joinToString(separator = ", ") { it.name }}") {
  override fun fillInStackTrace(): Throwable = this
  override val cause: Throwable? = this
}