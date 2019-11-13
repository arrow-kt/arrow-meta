package arrow.meta.ide.plugins.higherkinds

import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.ideTest
import arrow.meta.ide.testing.resolves
import org.junit.Test

class HigherKindTest : IdeTestSetUp() {
  @Test
  fun `LM Test`() =
    ideTest(
      IdeTest(
        code = HigherKindsTestCode.code1,
        myFixture = myFixture,
        test = { code, myFixture ->
          collectLM(code, myFixture, ArrowIcons.HKT)
        },
        result = resolves("LineMarkerTest for one valid HKT") {
          it.takeIf { description -> description.lineMarker.size == 1 && description.slowLM.isEmpty() }
        }
      )
    )

  @Test
  fun `LM for multipel HKT`() =
    ideTest(
      IdeTest(
        code = HigherKindsTestCode.code2,
        myFixture = myFixture,
        test = { code, myFixture ->
          collectLM(code, myFixture, ArrowIcons.HKT)
        },
        result = resolves("LineMarkerTest for multiple valid HKT") {
          it.takeIf { description -> description.lineMarker.size == 2 && description.slowLM.isEmpty() }
        }
      )
    )

  @Test
  fun `LM for no valid HKT`() =
    ideTest(
      IdeTest(
        code = HigherKindsTestCode.code3,
        myFixture = myFixture,
        test = { code, myFixture ->
          collectLM(code, myFixture, ArrowIcons.HKT)
        },
        result = resolves("LineMarkerTest for no valid HKT") {
          it.takeIf { description -> description.lineMarker.isEmpty() && description.slowLM.isEmpty() }
        }
      )
    )

  @Test
  fun `LM for Poly Icon`() =
    ideTest(
      IdeTest(
        code = HigherKindsTestCode.code4,
        myFixture = myFixture,
        test = { code, myFixture ->
          collectLM(code, myFixture, ArrowIcons.POLY)
        },
        result = resolves("LineMarkerTest for Poly Icon") {
          it.takeIf { description -> description.lineMarker.size == 2  && description.slowLM.isEmpty() }
        }
      )
    )
}