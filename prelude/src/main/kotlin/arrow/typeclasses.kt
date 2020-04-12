package arrowx

import arrow.Extension
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface Semigroup<A> {
  fun A.combine(other: A): A
  interface Syntax<A> {
    val value: A
    fun combine(other: A): A
  }
}


operator fun String.Companion.getValue(nothing: Nothing?, property: KProperty<*>): Monoid<String> =
  StringMonoid

operator fun String.getValue(nothing: Nothing?, property: KProperty<*>): Semigroup.Syntax<String> =
  StringSyntax(this)

interface Monoid<A> : Semigroup<A> {
  fun empty(): A
}

object StringMonoid : Monoid<String>, ReadOnlyProperty<String, Monoid<String>> {
  override fun String.combine(other: String): String = this + other
  override fun empty(): String = ""
  override fun getValue(thisRef: String, property: KProperty<*>): Monoid<String> = this
}

inline class StringSyntax(override val value: String) : Semigroup.Syntax<String>, ReadOnlyProperty<String, Semigroup.Syntax<String>> {
  override fun combine(other: String): String =
    value + other

  override fun getValue(thisRef: String, property: KProperty<*>): Semigroup.Syntax<String> = this
}

@Extension
fun String.Companion.monoid(): Monoid<String> =
  StringMonoid

@Extension
fun String.semigroupSyntax(): Semigroup.Syntax<String> =
  StringSyntax(this)

data class Id<A>(val value: A) {
  companion object
}

interface Applicative<F, A> : Kind<F, A> {
  val value: Kind<F, A>
  fun <B> map(f: (A) -> B): Kind<F, B>
  interface Companion<F> {
    fun <A> just(a: A): Kind<F, A>
  }
}

@Extension
fun Id.Companion.applicative(): Applicative.Companion<Id.Companion> =
  IdApplicative.Companion

@Extension
fun <A> Id<A>.applicative(): Applicative<Id.Companion, A> =
  IdApplicative(kind())

fun <A> Kind<Id.Companion, A>.fix(): Id<A> =
  (this as Kinded).value as Id<A>

fun <A> Id<A>.kind(): Kind<Id.Companion, A> =
  Kinded(this)

inline class IdApplicative<A>(override val value: Kind<Id.Companion, A>) : Applicative<Id.Companion, A> {
  override fun <B> map(f: (A) -> B): Kind<Id.Companion, B> =
    Id(f(value.fix().value)).kind()

  companion object : Applicative.Companion<Id.Companion> {
    override fun <A> just(a: A): Kind<Id.Companion, A> =
      Id(a).kind()
  }
}

