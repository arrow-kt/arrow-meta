package arrow.meta.idea.test.syntax.utils

import arrow.meta.dsl.platform.ide
import junit.framework.TestCase.assertNotNull

interface IdeTestSyntax {
  val lightTest: LightTestSyntax
    get() = LightTestSyntax

  companion object : IdeTestSyntax {
    operator fun <R> invoke(f: IdeTestSyntax.() -> R): Unit =
      assertNotNull("Test is run without an Ide instance", ide { f(this) })

    fun <R> lightTest(f: LightTestSyntax.() -> R): Unit = IdeTestSyntax { f(lightTest) }
  }
}