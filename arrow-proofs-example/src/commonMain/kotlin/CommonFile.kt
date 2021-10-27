package test

import arrow.Context

@Context
@Retention(AnnotationRetention.RUNTIME)
@Target(
  AnnotationTarget.CLASS,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.PROPERTY,
  AnnotationTarget.VALUE_PARAMETER
)
@MustBeDocumented
annotation class Given

@Context
@Retention(AnnotationRetention.RUNTIME)
@Target(
  AnnotationTarget.CLASS,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.PROPERTY,
  AnnotationTarget.VALUE_PARAMETER
)
@MustBeDocumented
annotation class Config

@Given object X {
  val value = "yes!"
}
@Config object Y {
  val value = "nope!"
}
fun foo(@Given x: X, @Config y: Y): Pair<String, String> =
  x.value to y.value
val result = foo()
