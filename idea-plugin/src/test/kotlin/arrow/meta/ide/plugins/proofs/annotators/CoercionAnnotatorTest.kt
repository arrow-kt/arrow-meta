package arrow.meta.ide.plugins.proofs.annotators

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.dsl.IdeTestSyntax
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.file
import arrow.meta.ide.testing.env.ideTest
import arrow.meta.ide.testing.env.types.LightTestSyntax.toKtFile
import com.intellij.codeHighlighting.Pass
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.psi.KtFile

class CoercionAnnotatorTest : IdeTestSetUp(
  CoercionAnnotatorTestCode.prelude.file("arrow/preludeCoercion.kt"),
  CoercionAnnotatorTestCode.twitterHandleDeclaration.file("consumer/consumerCoercion.kt")
) {

  @org.junit.Test
  fun `coercion annotator test`() =
    ideTest(
      myFixture = myFixture,
      ctx = IdeMetaPlugin()
    ) {
      listOf<IdeTest<IdeMetaPlugin, Pair<List<HighlightInfo>, Source>>>(
        IdeTest(
          code = CoercionAnnotatorTestCode.code1,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            collectAnnotationsApplyFix(code, myFixture)
          },
          result = resolvesWhen("CoercionAnnotatorTest1 for 1 explicit coercion property") { pairResult: Pair<List<HighlightInfo>, Source> ->
            pairResult.first.size == 1
              && pairResult.second == CoercionAnnotatorTestCode.code1_after_fix
          }
        ),
        IdeTest(
          code = CoercionAnnotatorTestCode.code2,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            collectAnnotationsApplyFix(code, myFixture)
          },
          result = resolvesWhen("CoercionAnnotatorTest2 for 1 implicit coercion property") { pairResult: Pair<List<HighlightInfo>, Source> ->
            pairResult.first.size == 1
              && pairResult.second == CoercionAnnotatorTestCode.code2_after_fix
          }
        ),
        IdeTest(
          code = CoercionAnnotatorTestCode.code3,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            collectAnnotationsApplyFix(code, myFixture)
          },
          result = resolvesWhen("CoercionAnnotatorTest3 for 2 implicit coercion valueArgs") { pairResult: Pair<List<HighlightInfo>, Source> ->
            pairResult.first.size == 2
              && pairResult.second == CoercionAnnotatorTestCode.code3_after_fix
          }
        ))
    }

  private fun IdeTestSyntax.collectAnnotationsApplyFix(
    code: Source,
    myFixture: CodeInsightTestFixture
  ): Pair<List<HighlightInfo>, Source> {
    val highlightInfos: List<HighlightInfo> = collectAnnotations(code, myFixture)
    val codeFixed: Source = highlightInfos.firstOrNull()?.fixFirstInspection(myFixture, code.toKtFile(myFixture)).orEmpty()
    return Pair(highlightInfos, codeFixed)
  }

  /**
   * Returns the aggregated highlighting information of the file.
   */
  private fun IdeTestSyntax.collectAnnotations(
    code: Source,
    myFixture: CodeInsightTestFixture,
    toIgnore: List<Int> = ignoredHighlightingAnnotator,
    changes: (KtFile) -> Boolean = { it.isScript() }
  ): List<HighlightInfo> =
    lightTest {
      code.toKtFile(myFixture)?.run { highlighting(myFixture, toIgnore, changes) }
        // TODO: might be better ways to filter the highlights we want to check on
        ?.filter { it.description?.contains("coercion") ?: false }
    }.orEmpty()

  private val ignoredHighlightingAnnotator: List<Int>
    get() = listOf(Pass.LINE_MARKERS, Pass.EXTERNAL_TOOLS, Pass.POPUP_HINTS, Pass.UPDATE_FOLDING, Pass.WOLF)

  private fun HighlightInfo.fixFirstInspection(myFixture: CodeInsightTestFixture, file: KtFile?): Source {
    quickFixActionMarkers
      .map { it.first.action }
      .firstOrNull { it.text.contains("Make coercion") }
      ?.let { localFixAction: IntentionAction ->
        myFixture.project.executeWriteCommand(localFixAction.text, null) {
          localFixAction.invoke(myFixture.project, myFixture.editor, file)
        }
      }
    return file?.text.orEmpty()
  }
}
