package arrow.meta.ide.testing

import arrow.meta.ide.dsl.IdeSyntax
import arrow.meta.ide.testing.env.ideTest
import arrow.meta.ide.testing.env.resolution.ResolutionSyntax
import com.intellij.testFramework.fixtures.CodeInsightTestFixture

typealias Source = String

/**
 * [IdeTest] eventually completes with a test result [A] and has [F] as its plugin context for dependencies and features.
 * [test] results with [A] defines what exact test is run based on the [code].
 * [result] describes the expected shape of [A] with a custom message.
 * [F] is the plugin context, where dependencies and features can be used to define [test] and/or [result].
 * @see IdeResolution, [ideTest]
 * There is an example in [ideTest] KDoc's.
 */
data class IdeTest<F : IdeSyntax, A>(
  val code: Source,
  val test: IdeEnvironment.(code: Source, myFixture: CodeInsightTestFixture, ctx: F) -> A,
  val result: IdeResolution<F, A>
)

/**
 * [transform] defines a valid representation of the test result [A], where [null] stands for a wrong or unexpected representation.
 * [ResolutionSyntax] facilitates extensions for [IdeResolution].
 * @see ideTest
 */
data class IdeResolution<F : IdeSyntax, A>(val message: String, val transform: F.(result: A) -> A?)
