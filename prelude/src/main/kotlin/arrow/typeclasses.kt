package arrowx

import arrow.Proof
import arrow.TypeProof

interface Semigroup<A> {
  fun A.combine(other: A): A
  interface Syntax<A> {
    val value: A
    fun combine(other: A): A
  }
}

interface Monoid<A> : Semigroup<A> {
  fun empty(): A
}

object StringMonoid : Monoid<String> {
  override fun String.combine(other: String): String = this + other
  override fun empty(): String = ""
}

inline class StringSyntax(override val value: String)
  : Semigroup.Syntax<String> {
  override fun combine(other: String): String =
    value + other
}

@Proof(TypeProof.Extension)
fun String.Companion.monoid(): Monoid<String> =
  StringMonoid

@Proof(TypeProof.Extension)
fun String.semigroupSyntax(): Semigroup.Syntax<String> =
  StringSyntax(this)

data class Id<A>(val value: A) {
  companion object
}

interface Applicative<F, A> : Kind<F, A> {
  fun <B> map(f: (A) -> B): Kind<F, B>
  interface Companion<F> {
    fun <A> just(a: A): Kind<F, A>
  }
}

@Proof(TypeProof.Extension)
fun Id.Companion.applicative(): Applicative.Companion<Id.Companion> =
  IdApplicative.Companion

@Proof(TypeProof.Extension)
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

