package arrow.with.n1

@Retention(AnnotationRetention.RUNTIME)
@Target(
  AnnotationTarget.TYPE,
  AnnotationTarget.TYPE_PARAMETER
)
annotation class with<out A>

val With: Nothing
  get() = TODO("Should have been replaced by Arrow Meta Compiler Plugin provided by [plugins { id 'io.arrow-kt.arrow' version 'x.x.x' }")

fun <A> with(evidence: @with<A> A = With): A =
  evidence

