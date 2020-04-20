package arrow.meta.ide.testing.dsl.folding

import arrow.meta.ide.dsl.editor.folding.FoldingSyntax
import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.dsl.IdeTestSyntax
import com.intellij.lang.folding.FoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.lang.folding.LanguageFolding
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

/**
 * [FoldingTestSyntax] provides test methods for [FoldingBuilder].
 */
interface FoldingTestSyntax {
  /**
   * [collectFolding] collects all folded regions in the given [code] example.
   * [match] reuse the same match function from [FoldingSyntax.addFoldingBuilder]
   */
  fun IdeTestSyntax.collectFolding(
    code: Source,
    myFixture: CodeInsightTestFixture,
    match: (KtTypeReference) -> Boolean
  ): List<FoldingDescriptor> =
    lightTest {
      code.ktFileToList(myFixture).collectFolding(myFixture.editor.document, match)
    }.orEmpty()

  /**
   * collects all [FoldingDescriptor] from a List of PsiElements.
   * @receiver is a KtFile, which is deconstructed into a List.
   */
  fun List<PsiElement>.collectFolding(
    document: Document,
    match: (KtTypeReference) -> Boolean
  ): List<FoldingDescriptor> =
    LanguageFolding.INSTANCE.allForLanguage(KotlinLanguage.INSTANCE)
      .filterNotNull()
      .flatMap { foldingBuilder: FoldingBuilder ->
        mapNotNull { element: PsiElement ->
          element.safeAs<KtTypeReference>()?.takeIf(match)?.let {
            foldingBuilder.buildFoldRegions(it.node, document).filterNotNull()
          }
        }
      }
      .flatten()
}