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
      listOf<IdeTest<LineMarkerDescription, IdeMetaPlugin>>(
        IdeTest(
          code = ComprehensionsTestCode.code1,
          test = { code, myFixture, _ ->
            collectLM(code, myFixture, ArrowIcons.BIND)
          },
          result = resolvesWith("LineMarkerTest for two LM on typed variables") {
            it.takeIf { descriptor -> descriptor.lineMarker.size == 2 && descriptor.slowLM.isEmpty() }
          }
        ),
        IdeTest(
          code = ComprehensionsTestCode.code2,
          test = { code, myFixture, _ ->
            collectLM(code, myFixture, ArrowIcons.BIND)
          },
          result = resolvesWith("LineMarkerTest for two LM on untyped variables") {
            it.takeIf { descriptor -> descriptor.lineMarker.size == 2 && descriptor.slowLM.isEmpty() }
          }
        ),
        IdeTest(
          code = ComprehensionsTestCode.code3,
          test = { code, myFixture, _ ->
            collectLM(code, myFixture, ArrowIcons.BIND)
          },
          result = resolvesWith("LineMarkerTest for six LM on untyped variables") {
            it.takeIf { descriptor -> descriptor.lineMarker.size == 6 && descriptor.slowLM.isEmpty() }
          }
        ),
        IdeTest(
          code = ComprehensionsTestCode.code4,
          test = { code, myFixture, _ ->
            collectLM(code, myFixture, ArrowIcons.BIND)
          },
          result = resolvesWith("LineMarkerTest for four LM on untyped variables") {
            it.takeIf { descriptor -> descriptor.lineMarker.size == 4 && descriptor.slowLM.isEmpty() }
          }
        ),
        IdeTest(
          code = ComprehensionsTestCode.code5,
          test = { code, myFixture, _ ->
            collectLM(code, myFixture, ArrowIcons.BIND)
          },
          result = resolvesWith("LineMarkerTest for zero LM ") {
            it.takeIf { descriptor -> descriptor.lineMarker.isEmpty() && descriptor.slowLM.isEmpty() }
          }
        ),
        IdeTest(
          code = ComprehensionsTestCode.code6,
          test = { code, myFixture, _ ->
            collectLM(code, myFixture, ArrowIcons.BIND)
          },
          result = resolvesWith("LineMarkerTest for no LM ") {
            it.takeIf { descriptor -> descriptor.lineMarker.isEmpty() && descriptor.slowLM.isEmpty() }
          }
        )
      )
    }
}