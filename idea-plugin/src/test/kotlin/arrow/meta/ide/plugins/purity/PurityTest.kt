package arrow.meta.ide.plugins.purity

import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.ideTest
import arrow.meta.ide.testing.resolves
import org.junit.Test

class PurityTest : IdeTestSetUp() {
  @Test
  fun `Impure Function`() =
    ideTest(
      IdeTest(
        myFixture,
        PurityTestCode.code1,
        test = { code, myFixture ->
          collectInspections(code, myFixture, listOf(purityInspection))
        },
        result = resolves("At least one impure Function that needs to be suspended") {
          it.filter { it.inspectionToolId == purityInspection.defaultFixText }
            .takeIf {
              it.isNotEmpty()
            }
        }
      )
    )
}