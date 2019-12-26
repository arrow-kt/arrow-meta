package arrowx

import arrow.Proof
import arrow.TypeProof

interface Applicative<out F> {
  fun <A> just(a: A): Kind<F, A>
}

interface ApplicativeOps<out F, out A> {
  val fa: Kind<F, A>
  fun <B> fmap(f: (A) -> B): Kind<F, B>
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

object IdApplicative: Applicative<ForId> {
  override fun <A> just(a: A): Kind<ForId, A> =
    Id(a).unfix()
}

class IdApplicativeOps<A>(override val fa: Kind<ForId, A>): ApplicativeOps<ForId, A> {
  override fun <B> fmap(f: (A) -> B): Kind<ForId, B> =
    Id(f(fa.fix().value)).unfix()
}

@Proof(TypeProof.Subtyping)
fun Id.Companion.applicative(): Applicative<ForId> =
  IdApplicative

@Proof(TypeProof.Subtyping)
fun <A> Kind<ForId, A>.syntax(): ApplicativeOps<ForId, A> =
  IdApplicativeOps(this)

interface Monoid<A> {
  fun mempty(): A
  interface Ext<A> {
    val value: A
    fun mcombine(b: A): A
  }
}

object StringMonoid : Monoid<String> {
  override fun mempty(): String = ""
}

inline class StringMonoidExt(override val value: String) : Monoid.Ext<String> {
  override fun mcombine(b: String): String = value + b
}

@Proof(TypeProof.Subtyping)
fun String.Companion.monoid(): Monoid<String> = StringMonoid

@Proof(TypeProof.Subtyping)
fun String.monoidExt(): Monoid.Ext<String> = StringMonoidExt(this)