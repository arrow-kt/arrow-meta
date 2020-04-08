package arrow.meta.ide.testing

import arrow.meta.ide.testing.dsl.IdeTestSyntax
import arrow.meta.ide.testing.env.ideTest
import com.intellij.testFramework.fixtures.CodeInsightTestFixture

typealias Source = String

/**
 * [IdeTest] is a polymorphic aggregation of one complete test suite.
 * It is polymorphic over the TestResult [A], which is exactly what we yield in [test].
 * [myFixture] is a key component of the underlying IntelliJ Testing environment.
 * [test] defines what exact test is run on the [code] there is an example in [ideTest] KDoc's
 * [result] describes the expected shape of [A] with a custom message.
 * @see IdeResolution, [ideTest]
 */
data class IdeTest<A>(
  val myFixture: CodeInsightTestFixture,
  val code: Source,
  val test: IdeTestEnvironment.(code: Source, myFixture: CodeInsightTestFixture) -> A,
  val result: IdeResolution<A>
)

object IdeTestEnvironment : IdeTestSyntax

/**
 * [transform] defines a valid representation of the TestResult of Type [A], where [null] stands for a wrong or unexpected representation.
 * [fails], [failsWith], [empty] and [resolves] facilitate syntactic sugar to express semantic implications of an expected result.
 * @see ideTest
 */
data class IdeResolution<A>(val message: String, val transform: (result: A) -> A? = { it })

/**
 * @see IdeResolution
 */
fun <A> failsWith(message: String, transform: (result: A) -> A?): IdeResolution<A> = IdeResolution(message, transform)

/**
 * @see IdeResolution
 */
fun <A> resolves(message: String, transform: (result: A) -> A?): IdeResolution<A> = IdeResolution(message, transform)

/**
 * @see IdeResolution
 */
fun <A> empty(): IdeResolution<A> = IdeResolution("Empty IdeResolution")

/**
 * @see IdeResolution
 */
fun <A> fails(): IdeResolution<A> = IdeResolution("Failing IdeResolution") { null }
