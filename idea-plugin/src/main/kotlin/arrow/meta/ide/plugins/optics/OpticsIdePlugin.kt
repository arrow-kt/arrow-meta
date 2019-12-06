package arrow.meta.ide.plugins.optics

import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.editor.lineMarker.LineMarkerSyntax
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.plugins.optics.isProductType
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.opticsIdePlugin: Plugin
  get() = "OpticsIdePlugin" {
    meta(
      addLineMarkerProvider(
        icon = ArrowIcons.OPTICS,
        transform = { it.safeAs<KtClass>()?.takeIf(::isProductType)?.identifyingElement },
        message = { "Optics" }
      )
    )
  }

class OpticsIdePlugin : LineMarkerProvider, LineMarkerSyntax {
  override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiElement>? =
    element.safeAs<KtClass>()?.takeIf(::isProductType)?.identifyingElement?.let {
      lineMarkerInfo(ArrowIcons.OPTICS, it, message = { "Optics" })
    }
}