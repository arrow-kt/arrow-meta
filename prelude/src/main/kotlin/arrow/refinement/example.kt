package arrow

interface Refined<A> {
  val validate: A.() -> Map<String, Boolean>
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class Refinement(
  val predicate: String
)

//val runtime = "admin"
//val x: TwitterHandle = TwitterHandle("@admin") //fail
//val y: TwitterHandle = TwitterHandle("@whatever") //success
//val z1: TwitterHandle = "@whatever" //
//val z2: TwitterHandle? = "@whatever"
//val z3: TwitterHandle? = "@admin" //
//val z3: TwitterHandle? = runtime // null






