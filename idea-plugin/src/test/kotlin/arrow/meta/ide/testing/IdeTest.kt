package arrow.meta.ide.testing

import arrow.meta.ide.testing.dsl.IdeTestSyntax
import arrow.meta.plugin.testing.Source

data class IdeTest(
  val code: Source,
  val assert: Companion.() -> Assert
) {
  companion object : Assert.Syntax, IdeTestSyntax
}

sealed class Assert {
  sealed class IdeResolution<A> : Assert() {
    data class Resolves<A>(val f: (A) -> Boolean) : IdeResolution<A>()
    data class FailsWith<A>(val f: (A) -> Boolean) : IdeResolution<A>()
    object Fails : IdeResolution<Nothing>()
  }

  object Empty : Assert()

  interface Syntax {
    val empty: Assert
      get() = Empty
    val fails: Assert
      get() = IdeResolution.Fails
    val resolves: Assert
      get() = IdeResolution.Resolves<Int> { true }

    fun <A> failsWith(f: (A) -> Boolean): Assert = IdeResolution.FailsWith(f)
    fun <A> resolves(f: (A) -> Boolean): Assert = IdeResolution.Resolves(f)
  }
}