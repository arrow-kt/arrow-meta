package arrow.meta.ide.testing.env

import arrow.meta.dsl.platform.ide
import junit.framework.TestCase.assertNotNull

/**
 * [IdeTestTypeSyntax] aggregates Ide Test Types
 */
interface IdeTestTypeSyntax {
  /**
   * LightTests run headless ide instances
   */
  fun <R> lightTest(f: LightTestSyntax.() -> R): Unit =
    assertNotNull("Test is run without an Ide instance", ide { f(LightTestSyntax) })
}
