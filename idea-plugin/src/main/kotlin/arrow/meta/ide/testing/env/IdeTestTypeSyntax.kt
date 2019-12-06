package arrow.meta.ide.testing.env

import arrow.meta.dsl.platform.ide
import arrow.meta.ide.testing.env.types.HeavyTestSyntax
import arrow.meta.ide.testing.env.types.LightTestSyntax

/**
 * [IdeTestTypeSyntax] ensures that each TestType is within an Idea instance and provides Tooling from
 * IntelliJ's Testing Environment
 */
interface IdeTestTypeSyntax {
  /**
   * LightTests run headless ide instances
   */
  fun <A> lightTest(f: LightTestSyntax.() -> A): A? =
    ide { f(LightTestSyntax) }

  /**
   * HeavyTests run for each Test a full idea instance, thus are very slow, but perfect to
   * verify MetaData related Tests, like [arrow.meta.ide.plugins.typeclasses.ideSyntheticBodyResolution] would need.
   * new Signature: f: HeavyTestSyntax.(KotlinCompileEnv) -> R
   */
  fun <A> heavyTest(f: HeavyTestSyntax.() -> A): A? =
    ide { f(HeavyTestSyntax) }
}
