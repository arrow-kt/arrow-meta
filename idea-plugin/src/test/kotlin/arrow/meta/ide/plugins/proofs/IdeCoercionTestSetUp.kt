package arrow.meta.ide.plugins.proofs

import arrow.meta.ide.plugins.proofs.markers.CoercionTestCode
import arrow.meta.ide.testing.env.IdeTestSetUp

abstract class IdeCoercionTestSetUp : IdeTestSetUp() {
  override fun setUp() {
    super.setUp()
    myFixture.addFileToProject("arrow/prelude.kt", CoercionTestCode.prelude)
    myFixture.addFileToProject("consumer/consumer.kt", CoercionTestCode.twitterHandleDeclaration)
  }
}