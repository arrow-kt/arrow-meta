package arrow.meta.ide.testing

import arrow.meta.ide.testing.dsl.IdeTestSyntax

typealias Source = String

data class IdeTest<A>(
  val code: Source,
  val test: IdeTestEnvironment.(code: Source) -> Unit,
  val result: IdeResolution.Companion.(A) -> IdeResolution<A>
)

object IdeTestEnvironment : IdeTestSyntax

sealed class IdeResolution<A> {
  data class Resolves<A>(val transform: (A) -> A?) : IdeResolution<A>()
  data class FailsWith<A>(val transform: (A) -> A?) : IdeResolution<A>()
  object Fails : IdeResolution<Unit>()
  object Empty : IdeResolution<Nothing>()
  companion object : IdeResolutionSyntax
}

interface IdeResolutionSyntax {
  val empty: IdeResolution<Nothing>
    get() = IdeResolution.Empty
  val fails: IdeResolution<Unit>
    get() = IdeResolution.Fails
  val resolves: IdeResolution<Int>
    get() = IdeResolution.Resolves { 0 }

  fun <A> failsWith(transform: (A) -> A?): IdeResolution<A> = IdeResolution.FailsWith(transform)
  fun <A> resolves(transform: (A) -> A?): IdeResolution<A> = IdeResolution.Resolves(transform)
}
