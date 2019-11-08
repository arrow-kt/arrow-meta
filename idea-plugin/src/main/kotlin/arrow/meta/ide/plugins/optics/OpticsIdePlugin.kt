package arrow.meta.ide.plugins.optics

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.IdePlugin
import arrow.meta.ide.invoke
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.plugins.optics.isProductType
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.opticsIdePlugin: IdePlugin
  get() = "OpticsIdePlugin" {
    meta(
      addLineMarkerProvider(
        icon = ArrowIcons.OPTICS,
        transform = { it.safeAs<KtClass>()?.takeIf(::isProductType)?.identifyingElement },
        message = { "Optics" }
      )
    )
  }
