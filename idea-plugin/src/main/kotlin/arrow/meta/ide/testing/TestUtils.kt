package arrow.meta.ide.testing

data class UnavailableService(val service: Class<*>) :
  RuntimeException("Unavailable Service ${service.name}") {
  override fun fillInStackTrace(): Throwable = this
  override val cause: Throwable? = this
}