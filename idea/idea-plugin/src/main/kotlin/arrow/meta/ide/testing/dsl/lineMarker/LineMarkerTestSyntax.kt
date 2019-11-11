package arrow.meta.ide.testing.dsl.lineMarker

import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.dsl.IdeTestSyntax
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviders
import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import org.jetbrains.kotlin.idea.KotlinLanguage
import javax.swing.Icon

data class LineMarkerDescription(val lineMarker: List<LineMarkerInfo<PsiElement>> = emptyList(), val slowLM: List<LineMarkerInfo<PsiElement>> = emptyList())

interface LineMarkerTestSyntax {

  fun IdeTestSyntax.collectLM(code: Source, myFixture: CodeInsightTestFixture, icon: Icon): LineMarkerDescription =
    lightTest {
      code.ktFileToList(myFixture).run { LineMarkerDescription(collectLM(icon), collectSlowLM(icon)) }
    } ?: LineMarkerDescription()

  fun List<PsiElement>.collectLM(icon: Icon): List<LineMarkerInfo<PsiElement>> =
    LineMarkerProviders.INSTANCE.allForLanguage(KotlinLanguage.INSTANCE)
      .mapNotNull { mapNotNull { p: PsiElement -> it.getLineMarkerInfo(p) } }.flatten()
      .filter { it.icon == icon }

  fun List<PsiElement>.collectSlowLM(icon: Icon): List<LineMarkerInfo<PsiElement>> {
    val r = mutableListOf<LineMarkerInfo<PsiElement>>()
    LineMarkerProviders.INSTANCE.allForLanguage(KotlinLanguage.INSTANCE)
      .mapNotNull { it.collectSlowLineMarkers(this@collectSlowLM, r) }
    return r.filter { it.icon == icon }
  }
}
