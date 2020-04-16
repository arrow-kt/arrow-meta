package arrow.meta.ide.testing.dsl.folding

import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.dsl.IdeTestSyntax
import com.intellij.lang.folding.FoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.lang.folding.LanguageFolding
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

/**
 * [FoldingRegions] represents the folded regions.
 */
data class FoldingRegions(val foldingRegions: List<FoldingDescriptor> = emptyList())

/**
 * [FoldingTestSyntax] provides test methods for [FoldingBuilder].
 */
interface FoldingTestSyntax {
  /**
   * [collectFR] collects all folded regions in the given [code] example.
   * [type]: Union, Tuple, HKT...
   */
  fun IdeTestSyntax.collectFR(code: Source, myFixture: CodeInsightTestFixture, match: (KtTypeReference) -> Boolean): FoldingRegions =
    lightTest {
      val file = code.toKtFile(myFixture)!!
      runWriteAction {
        myFixture.openFileInEditor(file.virtualFile)
        myFixture.editor.document.setText(code)
      }
      code.ktFileToList(myFixture).run {
        FoldingRegions(collectFR(myFixture.editor.document, match))
      }
    } ?: FoldingRegions()

  /**
   * collects all [FoldingDescriptor] from a List of PsiElements.
   * @receiver is a KtFile, which is deconstructed into a List.
   */
  fun List<PsiElement>.collectFR(document: Document, match: (KtTypeReference) -> Boolean): List<FoldingDescriptor> =
    LanguageFolding.INSTANCE.allForLanguage(KotlinLanguage.INSTANCE)
      .mapNotNull { foldingBuilder: FoldingBuilder ->
        mapNotNull { p: PsiElement ->
          p.safeAs<KtTypeReference>()?.let {
            if (match(it))
              foldingBuilder.buildFoldRegions(it.node, document).toList()
            else emptyList()
          }
        }.flatten()
      }.flatten()
}