package arrow

import arrow.TypeProof.*
import arrowx.Kind
import arrowx.Kinded

/**
 * An ordinary type class
 */
interface Applicative<F> {
  fun <A> just(a: A): Kind<F, A>
  interface Ops<out F, out A> : Kind<F, A> {
    fun <B> map(f: (A) -> B): Kind<F, B>
  }
}

/**
 * A data type
 */
class Id<out A>(val value: A) {
  companion object
}

/**
 * An arbitrary datatype kinded marker for ad-hoc polymorphism
 */
typealias `Id(_)` = Id.Companion

/**
 * [Applicative.Ops] syntax is available for all [Id<A>]
 */
@Proof(Subtyping)
fun <A> Id<A>.syntax(): Applicative.Ops<`Id(_)`, A> =
  object : Applicative.Ops<`Id(_)`, A> {
    override fun <B> map(f: (A) -> B): Kind<`Id(_)`, B> =
      Kinded(Id(f(value)))
  }

/**
 * [Applicative] constructors is available to target [Id]
 */
@Proof(Subtyping)
fun `Id(_)`.applicative(): Applicative<`Id(_)`> =
  object : Applicative<`Id(_)`> {
    override fun <A> just(a: A): Kind<`Id(_)`, A> =
      Kinded(Id(a))
  }
