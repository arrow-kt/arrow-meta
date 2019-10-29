package arrow.meta.ide.testing.dsl.lineMarker

import arrow.meta.ide.dsl.utils.IdeUtils
import arrow.meta.ide.testing.Source
import arrow.meta.ide.testing.dsl.IdeTestSyntax
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviders
import com.intellij.psi.PsiElement
import com.intellij.psi.SyntaxTraverser
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.junit.Assert
import javax.swing.Icon

data class IconDescription(val icon: Icon, val name: String)

interface LineMarkerTestSyntax {
  fun IdeTestSyntax.testLineMarkers(
    code: Source,
    icon: IconDescription,
    slowLM: (List<LineMarkerInfo<PsiElement>>) -> List<LineMarkerInfo<PsiElement>>?,
    lm: (List<LineMarkerInfo<PsiElement>>) -> List<LineMarkerInfo<PsiElement>>?): Unit =
    availableLM(code, icon.icon) { psi ->
      Assert.assertNotNull("SlowLineMarkerTest for ${icon.name} failed", slowLM(psi.collectSlowLM(icon.icon)))
      Assert.assertNotNull("LineMarkerTest for ${icon.name} failed", lm(psi.collect(icon.icon)))
    }

  fun IdeTestSyntax.availableLM(code: Source, icon: Icon, f: LineMarkerTestSyntax.(PsiElement) -> Unit): Unit =
    lightTest {
      code.sequence { psi ->
        psi.firstChild?.let { first ->
          SyntaxTraverser.psiTraverser().children(PsiTreeUtil.getDeepestFirst(first))
            .filter { IdeUtils.isNotNull(it.firstChild) }.forEach { f(this@LineMarkerTestSyntax, it) }
        } ?: f(this@LineMarkerTestSyntax, psi)
      }
    }

  fun PsiElement.collect(icon: Icon): List<LineMarkerInfo<PsiElement>> =
    LineMarkerProviders.INSTANCE.allForLanguage(KotlinLanguage.INSTANCE)
      .mapNotNull { it.getLineMarkerInfo(this) }
      .filter { it.icon == icon }

  fun PsiElement.collectSlowLM(icon: Icon): List<LineMarkerInfo<PsiElement>> =
    mutableListOf<LineMarkerInfo<PsiElement>>().apply {
      LineMarkerProviders.INSTANCE.allForLanguage(KotlinLanguage.INSTANCE)
        .mapNotNull { it.collectSlowLineMarkers(listOf(this@collectSlowLM), this) }
    }.filter { it.icon == icon }
}
