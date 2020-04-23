package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.dsl.lineMarker.LineMarkerDescription
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.ideTest
import com.intellij.testFramework.fixtures.CodeInsightTestFixture

class CoercionTest : IdeTestSetUp() {
  @org.junit.Test
  fun `test coercion line marker`() =
    ideTest(
      myFixture = myFixture,
      ctx = IdeMetaPlugin()
    ) {
      listOf<IdeTest<IdeMetaPlugin, LineMarkerDescription>>(
        IdeTest(
          code = CoercionTestCode.code1,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            myFixture.addFileToProject("consumer/consumer.kt", CoercionTestCode.twitterHandleDeclaration)
            myFixture.addFileToProject("arrow/prelude.kt", CoercionTestCode.prelude)
            collectLM(code, myFixture, ArrowIcons.ICON4)
          },
          result = resolvesWhen("LineMarkerTest for 1 LM ") { descriptor ->
            descriptor.lineMarker.size == 1 && descriptor.slowLM.isEmpty()
          }
        )
      )
    }
}
