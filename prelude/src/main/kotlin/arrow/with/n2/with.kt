package arrow.with.n2

@Retention(AnnotationRetention.RUNTIME)
@Target(
  AnnotationTarget.TYPE,
  AnnotationTarget.TYPE_PARAMETER
)
annotation class with<out A, out B>