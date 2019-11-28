package arrow.meta.ide.testing

import arrow.meta.ide.testing.dsl.IdeTestSyntax
import com.intellij.testFramework.fixtures.CodeInsightTestFixture

typealias Source = String

/**
 * [IdeTest] is a polymorphic aggregation of one complete test suite.
 * [myFixture] is a key component of the underlying IntelliJ Testing environment.
 * [test] defines what exact test is run on the [code]
 * [result] describes the expected shape of [A] with a costume message.
 */
data class IdeTest<A>(
  val myFixture: CodeInsightTestFixture,
  val code: Source,
  val test: IdeTestEnvironment.(code: Source, myFixture: CodeInsightTestFixture) -> A,
  val result: IdeResolution<A>
)

object IdeTestEnvironment : IdeTestSyntax

/**
 * [transform] defines a valid representation of the editor feature, where [null] stands for a wrong representation.
 * [fails], [failsWith], [empty] and [resolves] facilitate syntactic sugar to express semantic implications of an expected result.
 */
data class IdeResolution<A>(val message: String, val transform: (A) -> A? = { it })

/**
 * @see IdeResolution
 */
fun <A> failsWith(message: String, transform: (A) -> A?): IdeResolution<A> = IdeResolution(message, transform)

/**
 * @see IdeResolution
 */
fun <A> resolves(message: String, transform: (A) -> A?): IdeResolution<A> = IdeResolution(message, transform)

/**
 * @see IdeResolution
 */
fun <A> empty(): IdeResolution<A> = IdeResolution("Empty IdeResolution")

/**
 * @see IdeResolution
 */
fun <A> fails(): IdeResolution<A> = IdeResolution("Failing IdeResolution") { null }
