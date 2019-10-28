package arrow.meta.ide.testing.env

import arrow.meta.dsl.platform.ide
import junit.framework.TestCase.assertNotNull

interface IdeTestTypeSyntax {
  /**
   * LightTests run headless ide instances
   */
  fun <R> lightTest(f: LightTestSyntax.() -> R): Unit =
    assertNotNull("Test is run without an Ide instance", ide { f(object : LightTestSyntax() {}) })

  /**
   * HeavyTests run for each Test a full idea instance, thus are very slow, but perfect to
   * verify MetaData related Tests, like [arrow.meta.ide.plugins.typeclasses.ideSyntheticBodyResolution] would need.
   */
  fun <R> heavyTest(f: HeavyTestSyntax.() -> R): Unit =
    assertNotNull("Test is run without an Ide instance", ide { f(object : HeavyTestSyntax() {}) })
}
