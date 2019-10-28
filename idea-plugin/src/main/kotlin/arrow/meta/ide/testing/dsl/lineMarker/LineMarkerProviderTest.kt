package arrow.meta.ide.testing.dsl.lineMarker

import arrow.meta.ide.testing.env.IdeTestTypeSyntax
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviders
import com.intellij.psi.PsiElement
import com.intellij.psi.SyntaxTraverser
import org.jetbrains.kotlin.idea.KotlinLanguage
import javax.swing.Icon

interface LineMarkerProviderTest {

  fun IdeTestTypeSyntax.availableLM(icon: Icon, code: String): Unit =
    lightTest { code.sequence { SyntaxTraverser.psiTraverser().children(it) } }



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
