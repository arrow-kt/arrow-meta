package arrow.meta.ide.testing

import arrow.meta.ide.testing.dsl.IdeTestSyntax
import com.intellij.testFramework.fixtures.CodeInsightTestFixture

typealias Source = String

data class IdeTest<A>(
  val myFixture: CodeInsightTestFixture,
  val code: Source,
  val test: IdeTestEnvironment.(code: Source, myFixture: CodeInsightTestFixture) -> A,
  val result: IdeResolution<A>
)

object IdeTestEnvironment : IdeTestSyntax

data class IdeResolution<A>(val message: String, val transform: (A) -> A? = { it })

fun <A> failsWith(message: String, transform: (A) -> A?): IdeResolution<A> = IdeResolution(message, transform)
fun <A> resolves(message: String, transform: (A) -> A?): IdeResolution<A> = IdeResolution(message, transform)
fun <A> empty(): IdeResolution<A> = IdeResolution("Empty IdeResolution")
fun <A> fails(): IdeResolution<A> = IdeResolution("Failing IdeResolution") { null }
