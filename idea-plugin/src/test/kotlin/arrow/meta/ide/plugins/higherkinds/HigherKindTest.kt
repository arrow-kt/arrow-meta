package arrow.meta.ide.plugins.higherkinds

import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.env.runTest
import arrow.meta.ide.testing.resolves
import org.junit.Test

class HigherKindTest {
  @Test
  fun `LM Test`() =
    IdeTest(
      code = HigherKindsTestCode.code,
      test = { code ->
        testLineMarkers(
          code,
          ArrowIcons.HKT,
          lm = { lm ->
            lm.takeIf { it.size == 1 }
          },
          slowLM = { slowLm ->
            slowLm.takeIf { it.isEmpty() }
          }
        )
      },
      result = resolves("LineMarker Test") { lmDescription ->
        lmDescription.takeIf {
          it.all { (line, slow) ->
            line != null && line.isNotEmpty()
          }
        }
      }
    ).runTest()
}