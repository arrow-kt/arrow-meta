package arrow.meta.ide.testing.dsl.lineMarker

import arrow.meta.ide.testing.env.IdeTestTypeSyntax
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviders
import com.intellij.psi.PsiElement
import com.intellij.psi.SyntaxTraverser
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.KotlinLanguage
import javax.swing.Icon

interface LineMarkerProviderTest {

  fun IdeTestTypeSyntax.availableLM(icon: Icon, code: String): Unit = lightTest {
    // if possible, line marker providers should provide icons for leaf elements
    // as advised by com.intellij.codeInsight.daemon.LineMarkerProvider.getLineMarkerInfo().
    // Therefore, we retrieve the leaf element for the current element and call the icon providers on this leaf
    code.sequence { psi ->
      val leaf = if (psi.firstChild == null) psi else PsiTreeUtil.getDeepestFirst(psi)
      if (leaf != psi) {
        SyntaxTraverser.psiTraverser().children(psi).filter { it.firstChild != null }.forEach { e ->
          /*          assertEmpty("no faster markers expected, element: ${e.text}", psi.collect(icon))

                    assertEmpty("no slow markers expected, element: ${e.text}", psi.collectSlowLM(icon))*/
        }
      }
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

/*  fun unavailableLM(icon: Icon, code: String): Unit =
  code.sequence { psi ->
    assertEmpty("no LineMarkers expected for ${psi.text}", psi.collect(icon))
    assertEmpty("no SlowLineMarker for ${psi.text}", psi.collectSlowLM(icon))
  }*/
}
