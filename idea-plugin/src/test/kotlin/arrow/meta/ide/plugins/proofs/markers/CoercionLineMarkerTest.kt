package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.inspections.CoercionInspectionTestCode
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.dsl.lineMarker.LineMarkerDescription
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.file
import arrow.meta.ide.testing.env.ideTest
import com.intellij.testFramework.fixtures.CodeInsightTestFixture

class CoercionLineMarkerTest :
  IdeTestSetUp(
    CoercionInspectionTestCode.prelude.file("arrow/preludeCoercion.kt"),
    CoercionLineMarkerTestCode.twitterHandleDeclaration.file("consumer/consumerCoercion.kt")
  ) {

  @org.junit.Test
  fun `coercion line marker test`() =
    ideTest(
      myFixture = myFixture,
      ctx = IdeMetaPlugin()
    ) {
      listOf<IdeTest<IdeMetaPlugin, LineMarkerDescription>>(
        IdeTest(
          code = CoercionLineMarkerTestCode.code1,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            collectLM(code, myFixture, ArrowIcons.COERCION_ICON)
          },
          result = resolvesWhen("CoercionLineMarkerTest1 for 2 LM ") { descriptor: LineMarkerDescription ->
            descriptor.lineMarker.size == 2 && descriptor.slowLM.isEmpty() &&
              descriptor.lineMarker[0].lineMarkerTooltip?.contains("String can be used in place of") ?: false
          }
        ),
        IdeTest(
          code = CoercionLineMarkerTestCode.code2,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            collectLM(code, myFixture, ArrowIcons.COERCION_ICON)
          },
          result = resolvesWhen("CoercionLineMarkerTest2 for 1 LM ") { descriptor: LineMarkerDescription ->
            descriptor.lineMarker.size == 1 && descriptor.slowLM.isEmpty() &&
              descriptor.lineMarker[0].lineMarkerTooltip?.contains("String can be used in place of Int? as if Int? : String") ?: false
          }
        ))
    }
}
