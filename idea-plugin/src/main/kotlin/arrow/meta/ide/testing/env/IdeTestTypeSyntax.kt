package arrow.meta.ide.testing.env

import arrow.meta.dsl.platform.ide
import arrow.meta.ide.testing.env.types.HeavyTestSyntax
import arrow.meta.ide.testing.env.types.LightTestSyntax
import junit.framework.TestCase.assertNotNull

/**
 * [IdeTestTypeSyntax] ensures that each TestType is within an Idea instance and provides Tooling from
 * IntelliJ's Testing
 */
interface IdeTestTypeSyntax {
  /**
   * LightTests run headless ide instances
   */
  fun <R> lightTest(f: LightTestSyntax.() -> R): R? =
    ide { f(LightTestSyntax) }

  /**
   * HeavyTests run for each Test a full idea instance, thus are very slow, but perfect to
   * verify MetaData related Tests, like [arrow.meta.ide.plugins.typeclasses.ideSyntheticBodyResolution] would need.
   */
  fun <R> heavyTest(f: HeavyTestSyntax.() -> R): Unit =
    assertNotNull("HeavyTests are run within Ide Environments", ide { f(HeavyTestSyntax) })
}
