package arrow

/**
 * A [Proof] establishes a relationship of trust between an extension function and the Kotlin compiler.
 *
 * [TypeProof]'s can establish [Subtyping] relationship between types, enhance the member scope of a type with [Extension]'s or
 * perform type [Refinement] among other constrains.
 */
enum class TypeProof {
  /**
   * ```kotlin:ank:silent
   * import arrow.Proof
   * import arrow.TypeProof
   *
   * inline class PositiveInt(val value: Int)
   *
   * @Proof(of = [Subtyping])
   * fun PositiveInt.toInt(): Int = value
   *
   * @Proof(of = [Subtyping])
   * fun Int.toPositiveInt(): PositiveInt? =
   * ```
   */
  Subtyping,
  Extension,
  Refinement,
  Negation
}

@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Proof(
  vararg val of: TypeProof = []
)