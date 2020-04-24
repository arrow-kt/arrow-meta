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

  override fun setUp() {
    super.setUp()
    myFixture.addFileToProject("consumer/consumer.kt", CoercionTestCode.twitterHandleDeclaration)
    myFixture.addFileToProject("arrow/prelude.kt", CoercionTestCode.prelude)
  }

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
            collectLM(code, myFixture, ArrowIcons.ICON4)
          },
          result = resolvesWhen("CoercionTest for 2 LM ") { descriptor ->
            println("CoercionTest1 Result[${descriptor.lineMarker.size}]=${descriptor.lineMarker}")
            descriptor.lineMarker.size == 2 && descriptor.slowLM.isEmpty()
          }
        ),
        IdeTest(
          code = CoercionTestCode.code2,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            collectLM(code, myFixture, ArrowIcons.ICON4)
          },
          result = resolvesWhen("CoercionTest for 0 LM ") { descriptor ->
            println("CoercionTest2 Result[${descriptor.lineMarker.size}]=${descriptor.lineMarker}")
            descriptor.lineMarker.isEmpty() && descriptor.slowLM.isEmpty()
          }
        ),
        IdeTest(
          code = CoercionTestCode.code3,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            collectLM(code, myFixture, ArrowIcons.ICON4)
          },
          result = resolvesWhen("CoercionTest for 1 LM ") { descriptor ->
            println("CoercionTest3 Result[${descriptor.lineMarker.size}]=${descriptor.lineMarker}")
            descriptor.lineMarker.size == 1 && descriptor.slowLM.isEmpty()
          }
        )
      )
    }
}
