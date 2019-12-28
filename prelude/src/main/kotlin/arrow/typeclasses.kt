package arrowx

import arrow.Proof
import arrow.TypeProof

interface Semigroup<A> {
  fun combine(other: A): A
}

interface Monoid<A> : Semigroup<A> {
  val value: A
  interface Companion<A> {
    fun empty(): A
  }
}

inline class StringMonoid(override val value: String) : Monoid<String> {
  override fun combine(other: String): String = value + other
  companion object : Monoid.Companion<String> {
    override fun empty(): String = ""
  }
}

@Proof(TypeProof.Subtyping)
fun String.Companion.monoid(): Monoid.Companion<String> = StringMonoid.Companion

@Proof(TypeProof.Subtyping)
fun String.monoidSyntax(): Monoid<String> = StringMonoid(this)

data class Id<A>(val value: A) {
  companion object
}

interface Applicative<F, A> : Kind<F, A> {
  fun <B> map(f: (A) -> B): Kind<F, B>
  interface Companion<F> {
    fun <A> just(a: A): Kind<F, A>
  }
}

@Proof(TypeProof.Subtyping)
fun Id.Companion.applicative(): Applicative.Companion<Id.Companion> =
  IdApplicative.Companion

@Proof(TypeProof.Subtyping)
fun <A> Id<A>.applicative(): Applicative<Id.Companion, A> =
  IdApplicative(this)

fun <A> Kind<Id.Companion, A>.fix(): Id<A> =
  (this as Kinded).value as Id<A>

fun <A> Id<A>.kind(): Kind<Id.Companion, A> =
  Kinded(this)

inline class IdApplicative<A>(val id: Id<A>) : Applicative<Id.Companion, A> {
  override fun <B> map(f: (A) -> B): Kind<Id.Companion, B> =
    Id(f(id.value)).kind()
  companion object : Applicative.Companion<Id.Companion> {
    override fun <A> just(a: A): Kind<Id.Companion, A> =
      Id(a).kind()
  }
}

fun <A: Monoid<A>> A.mappend(b: A): A =
  combine(b)
