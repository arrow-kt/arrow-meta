package arrow.with.n2

@Retention(AnnotationRetention.RUNTIME)
@Target(
  AnnotationTarget.TYPE,
  AnnotationTarget.TYPE_PARAMETER
)
annotation class with2<out A, out B>