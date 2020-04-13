package arrow.meta.ide.plugins.comprehensions

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.dsl.lineMarker.LineMarkerDescription
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.ideTest
import org.junit.Test

class ComprehensionsTest : IdeTestSetUp() {
  @Test
  fun `ComprehensionsTest for LineMarkers`(): Unit =
    ideTest(
      myFixture = myFixture,
      ctx = IdeMetaPlugin()
    ) {
      listOf<IdeTest<IdeMetaPlugin, LineMarkerDescription>>(
        IdeTest(
          code = ComprehensionsTestCode.code1,
          test = { code, myFixture, _ ->
            collectLM(code, myFixture, ArrowIcons.BIND)
          },
          result = resolvesWhen("LineMarkerTest for two LM on typed variables") {
            it.lineMarker.size == 2 && it.slowLM.isEmpty()
          }
        ),
        IdeTest(
          code = ComprehensionsTestCode.code2,
          test = { code, myFixture, _ ->
            collectLM(code, myFixture, ArrowIcons.BIND)
          },
          result = resolvesWhen("LineMarkerTest for two LM on untyped variables") {
            it.lineMarker.size == 2 && it.slowLM.isEmpty()
          }
        ),
        IdeTest(
          code = ComprehensionsTestCode.code3,
          test = { code, myFixture, _ ->
            collectLM(code, myFixture, ArrowIcons.BIND)
          },
          result = resolvesWhen("LineMarkerTest for six LM on untyped variables") {
            it.lineMarker.size == 6 && it.slowLM.isEmpty()
          }
        ),
        IdeTest(
          code = ComprehensionsTestCode.code4,
          test = { code, myFixture, _ ->
            collectLM(code, myFixture, ArrowIcons.BIND)
          },
          result = resolvesWhen("LineMarkerTest for four LM on untyped variables") {
            it.lineMarker.size == 4 && it.slowLM.isEmpty()
          }
        ),
        IdeTest(
          code = ComprehensionsTestCode.code5,
          test = { code, myFixture, _ ->
            collectLM(code, myFixture, ArrowIcons.BIND)
          },
          result = resolvesWhen("LineMarkerTest for zero LM ") {
            it.lineMarker.isEmpty() && it.slowLM.isEmpty()
          }
        ),
        IdeTest(
          code = ComprehensionsTestCode.code6,
          test = { code, myFixture, _ ->
            collectLM(code, myFixture, ArrowIcons.BIND)
          },
          result = resolvesWhen("LineMarkerTest for no LM ") {
            it.lineMarker.isEmpty() && it.slowLM.isEmpty()
          }
        )
      )
    }
}