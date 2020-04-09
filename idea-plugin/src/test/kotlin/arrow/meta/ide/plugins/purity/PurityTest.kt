package arrow.meta.ide.plugins.purity

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.ideTest
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import org.junit.Test

class PurityTest : IdeTestSetUp() {
  @Test
  fun `Impure Function`() =
    ideTest(
      myFixture = myFixture,
      ctx = IdeMetaPlugin()
    ) {
      listOf<IdeTest<List<HighlightInfo>, IdeMetaPlugin>>(
        IdeTest(
          PurityTestCode.code1,
          test = { code, myFixture, _ ->
            collectInspections(code, myFixture, listOf(purityInspection))
          },
          result = resolvesWith("At least one impure Function that needs to be suspended") {
            it.takeIf {
              it.any { info ->
                info.inspectionToolId == purityInspection.defaultFixText
              }
            }
          }
        )
      )
    }
}