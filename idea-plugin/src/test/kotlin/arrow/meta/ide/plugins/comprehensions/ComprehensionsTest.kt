package arrow.meta.ide.plugins.comprehensions

import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.ideTest
import arrow.meta.ide.testing.resolves
import org.junit.Test

class ComprehensionsTest : IdeTestSetUp() {
  @Test
  fun `Bind Test for two LM on typed variables`() =
    ideTest(
      IdeTest(
        code = ComprehensionsTestCode.code1,
        myFixture = myFixture,
        test = { code, myFixture ->
          collectLM(code, myFixture, ArrowIcons.BIND)
        },
        result = resolves("LineMarkerTest for two LM on typed variables"){
          it.takeIf { descriptor ->  descriptor.lineMarker.size == 2 && descriptor.slowLM.isEmpty()}
        }
      )
    )

  @Test
  fun `Bind Test for two LM on untyped variables`() =
    ideTest(
      IdeTest(
        code = ComprehensionsTestCode.code2,
        myFixture = myFixture,
        test = { code, myFixture ->
          collectLM(code, myFixture, ArrowIcons.BIND)
        },
        result = resolves("LineMarkerTest for two LM on untyped variables"){
          it.takeIf { descriptor ->  descriptor.lineMarker.size == 2 && descriptor.slowLM.isEmpty()}
        }
      )
    )

  @Test
  fun `Bind Test for six LM on untyped variables`() =
    ideTest(
      IdeTest(
        code = ComprehensionsTestCode.code3,
        myFixture = myFixture,
        test = { code, myFixture ->
          collectLM(code, myFixture, ArrowIcons.BIND)
        },
        result = resolves("LineMarkerTest for six LM on untyped variables"){
          it.takeIf { descriptor ->  descriptor.lineMarker.size == 6 && descriptor.slowLM.isEmpty()}
        }
      )
    )

  @Test
  fun `Bind Test for four LM on untyped variables`() =
    ideTest(
      IdeTest(
        code = ComprehensionsTestCode.code4,
        myFixture = myFixture,
        test = { code, myFixture ->
          collectLM(code, myFixture, ArrowIcons.BIND)
        },
        result = resolves("LineMarkerTest for four LM on untyped variables"){
          it.takeIf { descriptor ->  descriptor.lineMarker.size == 4 && descriptor.slowLM.isEmpty()}
        }
      )
    )

  @Test
  fun `Bind Test for zero LM`() =
    ideTest(
      IdeTest(
        code = ComprehensionsTestCode.code5,
        myFixture = myFixture,
        test = { code, myFixture ->
          collectLM(code, myFixture, ArrowIcons.BIND)
        },
        result = resolves("LineMarkerTest for zero LM "){
          it.takeIf { descriptor ->  descriptor.lineMarker.isEmpty() && descriptor.slowLM.isEmpty()}
        }
      )
    )

  @Test
  fun `Bind Test for no LM`() =
    ideTest(
      IdeTest(
        code = ComprehensionsTestCode.code6,
        myFixture = myFixture,
        test = { code, myFixture ->
          collectLM(code, myFixture, ArrowIcons.BIND)
        },
        result = resolves("LineMarkerTest for no LM "){
          it.takeIf { descriptor ->  descriptor.lineMarker.isEmpty() && descriptor.slowLM.isEmpty()}
        }
      )
    )
}