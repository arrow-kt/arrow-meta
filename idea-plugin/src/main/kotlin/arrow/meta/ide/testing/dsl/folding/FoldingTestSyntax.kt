package arrow.meta.ide.testing.dsl.folding

import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.dsl.IdeTestSyntax
import com.intellij.lang.folding.FoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import javax.swing.Icon

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
  fun <A> IdeTestSyntax.collectFR(code: Source, myFixture: CodeInsightTestFixture, type: A): FoldingRegions =
    lightTest {
      code.ktFileToList(myFixture).run { FoldingRegions(collectFR()) }
    } ?: FoldingRegions()

  /**
   * collects all [FoldingDescriptor] from a List of PsiElements.
   * @receiver is a KtFile, which is deconstructed into a List.
   */
  fun List<PsiElement>.collectFR(): List<FoldingDescriptor> = emptyList()
//    LineMarkerProviders.getInstance().allForLanguage(KotlinLanguage.INSTANCE)
//      .mapNotNull { mapNotNull { p: PsiElement -> it.getLineMarkerInfo(p) } }.flatten()
//      .filter { it.icon == icon }
}
