package arrow.meta.ide.plugins.proofs.annotators

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.utils.implicitProofAnnotatorTextAttributes
import arrow.meta.ide.testing.IdeTest
import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.dsl.IdeTestSyntax
import arrow.meta.ide.testing.env.IdeTestSetUp
import arrow.meta.ide.testing.env.file
import arrow.meta.ide.testing.env.ideTest
import com.intellij.codeHighlighting.Pass
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.jetbrains.kotlin.psi.KtFile

class GivenAnnotatorTest : IdeTestSetUp(
  GivenAnnotatorTestCode.givenPrelude.file("arrow/givenPrelude.kt"),
  GivenAnnotatorTestCode.givenProviders.file("consumer/givenProviders.kt")
) {

  @org.junit.Test
  fun `given annotator test`() =
    ideTest(
      myFixture = myFixture,
      ctx = IdeMetaPlugin()
    ) {
      listOf(
        IdeTest(
          code = GivenAnnotatorTestCode.code1,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            collectAnnotations(code, myFixture)
          },
          result = resolvesWhen("GivenAnnotatorTest1 for 2 given injectors") { result: List<HighlightInfo> ->
            result.size == 2 &&
              result.getOrNull(0)?.matches(
                textAttributes = implicitProofAnnotatorTextAttributes,
                highlightSeverity = HighlightSeverity.INFORMATION,
                containsActionText = "Go to proof: consumer.x",
                containsDescription = "is implicitly injected by given proof"
              ) == true &&
              result.getOrNull(1)?.matches(
                textAttributes = implicitProofAnnotatorTextAttributes,
                highlightSeverity = HighlightSeverity.INFORMATION,
                containsActionText = "Go to proof: consumer.y",
                containsDescription = "is implicitly injected by given proof"
              ) == true
          }
        ),
        IdeTest(
          code = GivenAnnotatorTestCode.code2,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            collectAnnotations(code, myFixture)
          },
          result = resolvesWhen("GivenAnnotatorTest2 for 4 given injectors") { result: List<HighlightInfo> ->
            result.size == 4 &&
              result.getOrNull(0)?.matches(
                textAttributes = implicitProofAnnotatorTextAttributes,
                highlightSeverity = HighlightSeverity.INFORMATION,
                containsActionText = "Go to proof: consumer.x",
                containsDescription = "Implicit injection by given proof"
              ) == true &&
              result.getOrNull(3)?.matches(
                textAttributes = implicitProofAnnotatorTextAttributes,
                highlightSeverity = HighlightSeverity.INFORMATION,
                containsActionText = "Go to proof: consumer.y",
                containsDescription = "Implicit injection by given proof"
              ) == true
          }
        ),
        IdeTest(
          code = GivenAnnotatorTestCode.code3,
          test = { code: Source, myFixture: CodeInsightTestFixture, _: IdeMetaPlugin ->
            collectAnnotations(code, myFixture)
          },
          result = resolvesWhen("GivenAnnotatorTest3 for 1 given injector") { result: List<HighlightInfo> ->
            result.size == 1 &&
              result.getOrNull(0)?.matches(
                textAttributes = implicitProofAnnotatorTextAttributes,
                highlightSeverity = HighlightSeverity.INFORMATION,
                containsActionText = "Go to proof: consumer.x",
                containsDescription = "Implicit injection by given proof"
              ) == true
          }
        ))
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
        ?.filter { it.description?.contains("given") ?: false }
    }.orEmpty()

  private val ignoredHighlightingAnnotator: List<Int>
    get() = listOf(Pass.LINE_MARKERS, Pass.EXTERNAL_TOOLS, Pass.POPUP_HINTS, Pass.UPDATE_FOLDING, Pass.WOLF)
}

internal fun HighlightInfo?.matches(
  textAttributes: TextAttributes,
  highlightSeverity: HighlightSeverity,
  containsActionText: String,
  containsDescription: String
): Boolean = this?.let {
  forcedTextAttributes == textAttributes &&
    severity == highlightSeverity &&
    quickFixActionRanges.getOrNull(0)?.first?.action?.text.orEmpty().contains(containsActionText) &&
    description.contains(containsDescription)
} ?: false