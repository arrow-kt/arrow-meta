package arrow.meta.idea.test.higherkinds

import arrow.meta.idea.test.syntax.IdeTestSyntax
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class IdeHigherKindsTest : IdeTestSyntax() {

  fun `TestLineMarker`() =
    assert(false == true)
}