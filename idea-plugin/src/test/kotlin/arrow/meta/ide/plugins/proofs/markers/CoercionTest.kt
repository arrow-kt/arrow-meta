package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.ideTest
import arrow.meta.ide.testing.resolves

class CoercionTest : IdeTestSetUp() {

  override fun setUp() {
    super.setUp()
    myFixture.addFileToProject("consumer/consumer.kt", CoercionTestCode.twitterHandleDeclaration)
  }

  @org.junit.Test
  fun `test coercion line marker`() =
    ideTest(
      IdeTest(
        code = CoercionTestCode.code1,
        myFixture = myFixture,
        test = { code, myFixture ->
          collectLM(code, myFixture, ArrowIcons.ICON4)
        },
        result =
        resolves("LineMarkerTest for 1 LM ") {
          it.takeIf { descriptor -> descriptor.lineMarker.size == 1 && descriptor.slowLM.isEmpty() }
        }
      )
    )
}
