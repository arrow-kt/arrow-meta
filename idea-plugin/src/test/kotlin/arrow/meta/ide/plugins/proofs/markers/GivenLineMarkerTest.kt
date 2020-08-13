package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.annotators.GivenAnnotatorTestCode
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.dsl.lineMarker.LineMarkerDescription
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.file
import arrow.meta.ide.testing.env.ideTest
import com.intellij.testFramework.fixtures.CodeInsightTestFixture

class GivenLineMarkerTest :
  IdeTestSetUp(
    GivenAnnotatorTestCode.givenPrelude.file("arrow/givenPrelude.kt")
  ) {

  @org.junit.Test
  fun `given line marker test`() =
    ideTest(
      myFixture = myFixture,
      ctx = IdeMetaPlugin()
    ) {
      listOf<IdeTest<IdeMetaPlugin, LineMarkerDescription>>(
        IdeTest(
          code = GivenLineMarkerTestCode.code1,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            collectLM(code, myFixture, ArrowIcons.GIVEN_ICON)
          },
          result = resolvesWhen("GivenLineMarkerTestCode1 for 2 LM ") { descriptor: LineMarkerDescription ->
            descriptor.lineMarker.size == 2 && descriptor.slowLM.isEmpty() &&
              descriptor.lineMarker[0].lineMarkerTooltip?.contains("as a call to this member") ?: false &&
              descriptor.lineMarker[1].lineMarkerTooltip?.contains("as a call to this member") ?: false
          }
        ),
        IdeTest(
          code = GivenLineMarkerTestCode.code2,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            collectLM(code, myFixture, ArrowIcons.GIVEN_ICON)
          },
          result = resolvesWhen("GivenLineMarkerTestCode2 for 1 LM ") { descriptor: LineMarkerDescription ->
            descriptor.lineMarker.size == 1 && descriptor.slowLM.isEmpty() &&
              descriptor.lineMarker[0].lineMarkerTooltip?.contains("as a call to this member") ?: false
          }
        ),
        IdeTest(
          code = GivenLineMarkerTestCode.code3,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            collectLM(code, myFixture, ArrowIcons.GIVEN_ICON)
          },
          result = resolvesWhen("GivenLineMarkerTestCode3 for 1 LM ") { descriptor: LineMarkerDescription ->
            descriptor.lineMarker.size == 1 && descriptor.slowLM.isEmpty() &&
              descriptor.lineMarker[0].lineMarkerTooltip?.contains("as a singleton value") ?: false
          }
        ),
        IdeTest(
          code = GivenLineMarkerTestCode.code4,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            collectLM(code, myFixture, ArrowIcons.GIVEN_ICON)
          },
          result = resolvesWhen("GivenLineMarkerTestCode4 for 1 LM ") { descriptor: LineMarkerDescription ->
            descriptor.lineMarker.size == 1 && descriptor.slowLM.isEmpty() &&
              descriptor.lineMarker[0].lineMarkerTooltip?.contains("as a new instance of this class") ?: false
          }
        ))
    }
}
