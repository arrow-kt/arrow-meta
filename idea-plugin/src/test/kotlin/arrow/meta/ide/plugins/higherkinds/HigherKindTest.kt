package arrow.meta.ide.plugins.higherkinds

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.dsl.lineMarker.LineMarkerDescription
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.ideTest
import org.junit.Test

class HigherKindTest : IdeTestSetUp() {
  @Test
  fun `HKT lineMarker tests`(): Unit =
    ideTest(
      myFixture = myFixture,
      ctx = IdeMetaPlugin()
    ) {
      listOf<IdeTest<IdeMetaPlugin, LineMarkerDescription>>(
        IdeTest(
          code = HigherKindsTestCode.code1,
          test = { code, myFixture, _ ->
            collectLM(code, myFixture, ArrowIcons.HKT)
          },
          result = resolvesWhen("LineMarkerTest for one valid HKT") {
            it.lineMarker.size == 1 && it.slowLM.isEmpty()
          }
        ),
        IdeTest(
          code = HigherKindsTestCode.code2,
          test = { code, myFixture, _ ->
            collectLM(code, myFixture, ArrowIcons.HKT)
          },
          result = resolvesWhen("LineMarkerTest for multiple valid HKT") {
            it.lineMarker.size == 2 && it.slowLM.isEmpty()
          }
        ),
        IdeTest(
          code = HigherKindsTestCode.code3,
          test = { code, myFixture, _ ->
            collectLM(code, myFixture, ArrowIcons.HKT)
          },
          result = resolvesWhen("LineMarkerTest for no valid HKT") {
            it.lineMarker.isEmpty() && it.slowLM.isEmpty()
          }
        )
      )
    }


  @Test
  fun `HKT poly lineMarker test`() =
    ideTest(
      myFixture = myFixture,
      ctx = IdeMetaPlugin()
    ) {
      listOf<IdeTest<IdeMetaPlugin, LineMarkerDescription>>(
        IdeTest(
          code = HigherKindsTestCode.code4,
          test = { code, myFixture, _ ->
            collectLM(code, myFixture, ArrowIcons.POLY)
          },
          result = resolvesWhen("LineMarkerTest for Poly Icon") {
            it.lineMarker.size == 2 && it.slowLM.isEmpty()
          }
        )
      )
    }
}