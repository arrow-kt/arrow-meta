package arrow.meta.ide.plugins.higherkinds

import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.env.assertThis
import arrow.meta.ide.testing.source
import org.junit.Test

class HigherKindTest {
  @Test
  fun `LM Test`(): Unit =
    assertThis(
      IdeTest(
        code = "HigherKindsTestCode.code".source,
        assert = {
          // availableLM(ArrowIcons.HKT, HigherKindsTestCode.code.source)
          empty
        }
      )
    )
}