package arrow.meta.ide.plugins.optics

import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.editor.lineMarker.addLineMarkerProvider
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.plugins.optics.isProductType
import org.jetbrains.kotlin.psi.KtClass

val IdeMetaPlugin.opticsIdePlugin: Plugin
  get() = "OpticsIdePlugin" {
    meta(
      addLineMarkerProvider<KtClass>(
        icon = ArrowIcons.OPTICS,
        transform = { it.takeIf(::isProductType) },
        message = { "Optics" }
      )
    )
  }
