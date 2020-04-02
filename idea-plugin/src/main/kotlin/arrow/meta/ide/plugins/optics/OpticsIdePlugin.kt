package arrow.meta.ide.plugins.optics

import arrow.meta.Plugin
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.invoke
import arrow.meta.plugins.optics.isProductType
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
