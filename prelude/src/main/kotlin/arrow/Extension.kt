package arrow

@Retention(AnnotationRetention.RUNTIME)
@Target(
  AnnotationTarget.CLASS,
  AnnotationTarget.LOCAL_VARIABLE,
  AnnotationTarget.FUNCTION
)
@MustBeDocumented
annotation class Extension
