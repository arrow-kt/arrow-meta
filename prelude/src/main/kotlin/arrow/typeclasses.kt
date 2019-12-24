package arrowx

import arrow.Proof
import arrow.TypeProof
import arrowx.Kind
import arrowx.Kinded

interface Applicative<C, F> {
  fun <A> C.just(a: A): Kind<F, A>
  fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>
}

class ForId

@Proof(TypeProof.Subtyping)
fun <A> Kind<ForId, A>.fix(): Id<A> =
  (this as Kinded).value as Id<A>

@Proof(TypeProof.Subtyping)
fun <A> Id<A>.unfix(): Kind<ForId, A> =
  Kinded(this)

class Id<out A>(val value: A) {
  companion object
}

object IdApplicative : Applicative<Id.Companion, ForId> {
  override fun <A, B> Kind<ForId, A>.map(f: (A) -> B): Kind<ForId, B> =
    Id(f(fix().value)).unfix()
  override fun <A> Id.Companion.just(a: A): Kind<ForId, A> =
    Id(a).unfix()
}

@Proof(TypeProof.Extension)
fun <A> Id<A>.applicative(): Applicative<Id.Companion, ForId> =
  IdApplicative

fun <A> Id.Companion.just2(a: A): Kind<ForId, A> = TODO()