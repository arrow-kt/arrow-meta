package arrow.meta.idea.test.higherkinds

import arrow.meta.idea.test.code.higherkinds.IdeHigherKindesTestCode
import arrow.meta.idea.test.syntax.IdeTest
import arrow.meta.plugin.testing.assertThis
import arrow.meta.plugin.testing.source
import org.junit.Test

class IdeHigherKindsTest {
  @Test
  fun `LM Test`() =
    assertThis(
      IdeTest(
        code = { IdeHigherKindesTestCode.code.source },
        assert = {

        }
      )
    )
}