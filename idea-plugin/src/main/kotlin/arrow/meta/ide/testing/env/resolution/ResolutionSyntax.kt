package arrow.meta.ide.testing.env.resolution

import arrow.meta.ide.dsl.IdeSyntax
import arrow.meta.ide.testing.IdeResolution
import arrow.meta.ide.testing.dsl.IdeTestSyntax


interface ResolutionSyntax {
  /**
   * semantic sugar to define an [IdeResolution] that given [transform] fails.
   * @param transform describes the premises or shape of [A]
   * @see IdeResolution
   */
  fun <F : IdeSyntax, A> IdeTestSyntax.failsWith(message: String, transform: F.(result: A) -> A?): IdeResolution<F, A> = IdeResolution(message, transform)

  /**
   * a convenience function to express [failsWith] in boolean logic.
   */
  fun <F : IdeSyntax, A> IdeTestSyntax.failsWhen(message: String, transform: F.(result: A) -> Boolean): IdeResolution<F, A> =
    failsWith(message) { a: A -> if (transform(a)) a else null }

  /**
   * semantic sugar to define an [IdeResolution] that given [transform] completes successfully.
   * @param transform describes the premises or shape of [A].
   * @see IdeResolution
   */
  fun <F : IdeSyntax, A> IdeTestSyntax.resolvesWith(message: String, transform: F.(result: A) -> A?): IdeResolution<F, A> = IdeResolution(message, transform)

  /**
   * a convenience function to express [resolvesWith] in boolean logic.
   */
  fun <F : IdeSyntax, A> IdeTestSyntax.resolvesWhen(message: String, transform: F.(result: A) -> Boolean): IdeResolution<F, A> =
    resolvesWith(message) { a: A -> if (transform(a)) a else null }

  /**
   * this extension denotes that any test result in the test environment is accepted as a valid output.
   * Thereby, the Ide resolution always completes successfully, regardless of [A].
   * @see IdeResolution
   */
  fun <F : IdeSyntax, A> IdeTestSyntax.resolves(message: String = "Any result is accepted."): IdeResolution<F, A> = IdeResolution(message) { it }

  /**
   * this extension denotes that any test result in the test environment fails.
   * Thereby, the Ide resolution always fails, regardless of [A].
   * @see IdeResolution
   */
  fun <F : IdeSyntax, A> IdeTestSyntax.fails(message: String = "Failing Resolution"): IdeResolution<F, A> = IdeResolution(message) { null }
}