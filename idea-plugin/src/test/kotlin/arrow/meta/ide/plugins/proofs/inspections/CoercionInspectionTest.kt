package arrow.meta.ide.plugins.proofs.inspections

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.coercions.explicit.COERCION_EXPLICIT_ARGS
import arrow.meta.ide.plugins.proofs.coercions.explicit.COERCION_EXPLICIT_PROP
import arrow.meta.ide.plugins.proofs.coercions.explicit.explicitCoercionKtProperty
import arrow.meta.ide.plugins.proofs.coercions.explicit.explicitCoercionKtValArg
import arrow.meta.ide.plugins.proofs.coercions.implicit.IMPLICIT_COERCION_INSPECTION_ID
import arrow.meta.ide.plugins.proofs.coercions.implicit.implicitCoercion
import arrow.meta.ide.plugins.proofs.markers.CoercionTestCode
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.dsl.IdeTestSyntax
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.ideTest
import arrow.meta.ide.testing.env.types.LightTestSyntax.toKtFile
import arrow.meta.log.Log
import arrow.meta.log.invoke
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInspection.InspectionProfileEntry
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.psi.KtFile

class CoercionInspectionTest : IdeTestSetUp() {

  override fun setUp() {
    super.setUp()
    myFixture.addFileToProject("arrow/prelude.kt", CoercionTestCode.prelude)
    myFixture.addFileToProject("consumer/consumer.kt", CoercionTestCode.twitterHandleDeclaration)
  }

  @org.junit.Test
  fun `coercion inspection test`() =
    ideTest(
      myFixture = myFixture,
      ctx = IdeMetaPlugin()
    ) {
      listOf<IdeTest<IdeMetaPlugin, Pair<List<HighlightInfo>, Source>>>(
        IdeTest(
          code = CoercionInspectionTestCode.code1,
          test = { code: Source, myFixture: CodeInsightTestFixture, ctx: IdeMetaPlugin ->
            collectAndApplyInspection(code, myFixture, listOf(ctx.explicitCoercionKtProperty), COERCION_EXPLICIT_PROP)
          },
          result = resolvesWhen("CoercionInspectionTest1 for 1 implicit coercion") { pairResult: Pair<List<HighlightInfo>, Source> ->
            pairResult.first.size == 1
              && pairResult.second == CoercionInspectionTestCode.code1_after_fix
          }
        ),
        IdeTest(
          code = CoercionInspectionTestCode.code2,
          test = { code: Source, myFixture: CodeInsightTestFixture, ctx: IdeMetaPlugin ->
            collectAndApplyInspection(code, myFixture, listOf(ctx.implicitCoercion), IMPLICIT_COERCION_INSPECTION_ID)
          },
          result = resolvesWhen("CoercionInspectionTest2 for 1 explicit coercion") { pairResult: Pair<List<HighlightInfo>, Source> ->
            pairResult.first.size == 1
              && pairResult.second == CoercionInspectionTestCode.code2_after_fix
          }
        ),
        IdeTest(
          code = CoercionInspectionTestCode.code3,
          test = { code: Source, myFixture: CodeInsightTestFixture, ctx: IdeMetaPlugin ->
            collectAndApplyInspection(code, myFixture, listOf(ctx.explicitCoercionKtValArg), COERCION_EXPLICIT_ARGS)
          },
          result = resolvesWhen("CoercionInspectionTest3 for 2 explicit coercion") { pairResult: Pair<List<HighlightInfo>, Source> ->
            pairResult.first.size == 2
              && pairResult.second == CoercionInspectionTestCode.code3_after_fix
          }
        ))
    }

  private fun IdeTestSyntax.collectAndApplyInspection(
    code: Source,
    myFixture: CodeInsightTestFixture,
    inspections: List<InspectionProfileEntry>,
    inspectionId: String
  ): Pair<List<HighlightInfo>, Source> {
    return try {
      val highlightInfos: List<HighlightInfo> = collectInspections(code, myFixture, inspections)
        .filter { it.inspectionToolId == inspectionId }
      val codeFixed: Source = highlightInfos.firstOrNull()?.fixFirstInspection(myFixture, code.toKtFile(myFixture)).orEmpty()
      Pair(highlightInfos, codeFixed)
    } catch (ex: Exception) {
      Log.Verbose({ "collectAndApplyInspection Error: ${ex.localizedMessage}, $this" }) {
        ex.printStackTrace()
      }
      Pair(emptyList(), "")
    }
  }
}

fun HighlightInfo.fixFirstInspection(myFixture: CodeInsightTestFixture, file: KtFile?): Source {
  quickFixActionMarkers
    .map { it.first.action }
    .firstOrNull()?.let { localFixAction: IntentionAction ->
      myFixture.project.executeWriteCommand(localFixAction.text, null) {
        localFixAction.invoke(myFixture.project, myFixture.editor, file)
      }
    }
  return file?.text.orEmpty()
}
