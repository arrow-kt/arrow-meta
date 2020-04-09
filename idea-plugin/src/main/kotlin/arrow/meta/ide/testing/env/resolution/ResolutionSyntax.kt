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
  fun <A, F : IdeSyntax> IdeTestSyntax.failsWith(message: String, transform: F.(result: A) -> A?): IdeResolution<A, F> = IdeResolution(message, transform)

  /**
   * semantic sugar to define an [IdeResolution] that given [transform] completes successfully.
   * @param transform describes the premises or shape of [A].
   * @see IdeResolution
   */
  fun <A, F : IdeSyntax> IdeTestSyntax.resolvesWith(message: String, transform: F.(result: A) -> A?): IdeResolution<A, F> = IdeResolution(message, transform)

  /**
   * this extension denotes that any test result in the test environment is accepted as a valid output.
   * Thereby, the Ide resolution always completes successfully, regardless of [A].
   * @see IdeResolution
   */
  fun <A, F : IdeSyntax> IdeTestSyntax.resolves(message: String = "Any result is accepted."): IdeResolution<A, F> = IdeResolution(message) { it }

  /**
   * this extension denotes that any test result in the test environment fails.
   * Thereby, the Ide resolution always fails, regardless of [A].
   * @see IdeResolution
   */
  fun <A, F : IdeSyntax> IdeTestSyntax.fails(message: String = "Failing Resolution"): IdeResolution<A, F> = IdeResolution(message) { null }
}