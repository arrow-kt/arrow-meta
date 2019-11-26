package arrow

import arrow.TypeProof.*
import arrowx.Kind
import arrowx.Kinded

interface Applicative<F> {
  fun <A> just(a: A): Kind<F, A>
  interface Ops<out F, out A> : Kind<F, A> {
    fun <B> map(f: (A) -> B): Kind<F, B>
  }
}

class `Id(_)`

class Id<out A>(val value: A) {
  companion object
}

@Proof(Extension)
fun <A> Id<A>.syntax(): Applicative.Ops<`Id(_)`, A> =
  object : Applicative.Ops<`Id(_)`, A> {
    override fun <B> map(f: (A) -> B): Kind<`Id(_)`, B> =
      Kinded(Id(f(value)))
  }

@Proof(Extension)
fun Id.Companion.applicative(): Applicative<`Id(_)`> =
  object : Applicative<`Id(_)`> {
    override fun <A> just(a: A): Kind<`Id(_)`, A> =
      Kinded(Id(a))
  }

fun <F, A, FA: Kind<F, A>>  FA.f(): Kind<F, A>
  where FA: Applicative.Ops<F, A> = TODO()

val x = Id(1).f()
