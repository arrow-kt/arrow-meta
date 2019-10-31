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
import javax.swing.Icon

data class LineMarkerDescription(val lineMarker: List<LineMarkerInfo<PsiElement>>?, val slowLM: List<LineMarkerInfo<PsiElement>>?)

interface LineMarkerTestSyntax {

  fun IdeTestSyntax.testLineMarkers(
    code: Source,
    icon: Icon,
    slowLM: (List<LineMarkerInfo<PsiElement>>) -> List<LineMarkerInfo<PsiElement>>?,
    lm: (List<LineMarkerInfo<PsiElement>>) -> List<LineMarkerInfo<PsiElement>>?): List<LineMarkerDescription> =
    availableLM(code, icon) { psi: PsiElement ->
      LineMarkerDescription(lm(psi.collect(icon)), slowLM(psi.collectSlowLM(icon)))
    }

  fun <A> IdeTestSyntax.availableLM(code: Source, icon: Icon, f: LineMarkerTestSyntax.(PsiElement) -> A): List<A> =
    lightTest {
      code.traverse { psi: PsiElement ->
        psi.firstChild?.let { first: PsiElement ->
          SyntaxTraverser.psiTraverser().children(PsiTreeUtil.getDeepestFirst(first)).toList()
            .filter { it != null && IdeUtils.isNotNull(it.firstChild) }
            .map { f(this@LineMarkerTestSyntax, it) }
        } ?: listOf(f(this@LineMarkerTestSyntax, psi))
      }.flatten()
    } ?: emptyList()

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
