package arrow.meta.ide.testing.dsl.lineMarker

import arrow.meta.ide.dsl.utils.IdeUtils
import arrow.meta.ide.testing.env.IdeTestTypeSyntax
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviders
import com.intellij.psi.PsiElement
import com.intellij.psi.SyntaxTraverser
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.KotlinLanguage
import javax.swing.Icon

interface LineMarkerTestSyntax {

  fun IdeTestTypeSyntax.testLineMarkers(
    code: String,
    icon: Icon,
    slowLM: (List<LineMarkerInfo<PsiElement>>) -> Boolean,
    lm: (List<LineMarkerInfo<PsiElement>>) -> Boolean): Unit =
    availableLM(code, icon) { psi ->

    }

  fun IdeTestTypeSyntax.availableLM(code: String, icon: Icon, f: LineMarkerTestSyntax.(PsiElement) -> Unit): Unit =
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
