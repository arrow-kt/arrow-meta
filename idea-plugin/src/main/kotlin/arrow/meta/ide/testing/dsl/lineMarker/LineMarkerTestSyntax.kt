package arrow.meta.ide.testing.dsl.lineMarker

import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.dsl.IdeTestSyntax
import arrow.meta.ide.dsl.editor.lineMarker.LineMarkerSyntax
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviders
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.jetbrains.kotlin.idea.KotlinLanguage
import arrow.meta.ide.testing.env.ideTest
import javax.swing.Icon

/**
 * [LineMarkerDescription] represents the collected slowLinMarker's and regular lineMarkers.
 */
data class LineMarkerDescription(val lineMarker: List<LineMarkerInfo<PsiElement>> = emptyList(), val slowLM: List<LineMarkerInfo<PsiElement>> = emptyList())

/**
 * [LineMarkerTestSyntax] provides test methods for [LineMarkerProvider].
 * One example is in [ideTest] KDoc.
 * @see [LineMarkerSyntax]
 */
interface LineMarkerTestSyntax {
  /**
   * collects all registered SlowLineMarkers and regular LineMarkers in the given [code] example.
   */
  fun IdeTestSyntax.collectLM(code: Source, myFixture: CodeInsightTestFixture, icon: Icon): LineMarkerDescription =
    lightTest {
      code.ktFileToList(myFixture).run { LineMarkerDescription(collectLM(icon), collectSlowLM(icon)) }
    } ?: LineMarkerDescription()

  /**
   * collects regular LineMarkers from a List of PsiElements.
   * @receiver is a KtFile, which is deconstructed into a List.
   */
  fun List<PsiElement>.collectLM(icon: Icon): List<LineMarkerInfo<PsiElement>> =
    LineMarkerProviders.INSTANCE.allForLanguage(KotlinLanguage.INSTANCE)
      .mapNotNull { mapNotNull { p: PsiElement -> it.getLineMarkerInfo(p) } }.flatten()
      .filter { it.icon == icon }

  /**
   * Similar to [collectLM] only that it collects SlowLineMarkers.
   */
  fun List<PsiElement>.collectSlowLM(icon: Icon): List<LineMarkerInfo<PsiElement>> {
    val r = mutableListOf<LineMarkerInfo<PsiElement>>()
    LineMarkerProviders.INSTANCE.allForLanguage(KotlinLanguage.INSTANCE)
      .mapNotNull { it.collectSlowLineMarkers(this@collectSlowLM, r) }
    return r.filter { it.icon == icon }
  }
}
