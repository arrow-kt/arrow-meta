package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.dsl.lineMarker.LineMarkerDescription
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.file
import arrow.meta.ide.testing.env.ideTest
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.junit.Ignore

class CoercionTest : IdeTestSetUp(
  CoercionTestCode.prelude.file("arrow/prelude.kt"),
  CoercionTestCode.twitterHandleDeclaration.file("consumer/consumer.kt")
) {

  // TODO: Add test for annotations
  @Ignore // line markers got replaced by annotations so this test isn't valid anymore
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
          result = resolvesWhen("CoercionTest1 for 2 LM ") { descriptor: LineMarkerDescription ->
            descriptor.lineMarker.size == 2 && descriptor.slowLM.isEmpty()
          }
        ),
        IdeTest(
          code = CoercionTestCode.code2,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            collectLM(code, myFixture, ArrowIcons.ICON4)
          },
          result = resolvesWhen("CoercionTest2 for 0 LM ") { descriptor: LineMarkerDescription ->
            descriptor.lineMarker.isEmpty() && descriptor.slowLM.isEmpty()
          }
        ),
        IdeTest(
          code = CoercionTestCode.code3,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            collectLM(code, myFixture, ArrowIcons.ICON4)
          },
          result = resolvesWhen("CoercionTest3 for 2 LM ") { descriptor: LineMarkerDescription ->
            descriptor.lineMarker.size == 2 && descriptor.slowLM.isEmpty()
          }
        ),
        IdeTest(
          code = CoercionTestCode.code4,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            collectLM(code, myFixture, ArrowIcons.ICON4)
          },
          result = resolvesWhen("CoercionTest4 for 2 LM ") { descriptor: LineMarkerDescription ->
            descriptor.lineMarker.size == 2 && descriptor.slowLM.isEmpty()
          }
        )
      )
    }
}
