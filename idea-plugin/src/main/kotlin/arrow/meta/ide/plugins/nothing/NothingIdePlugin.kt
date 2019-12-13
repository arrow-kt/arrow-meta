package arrow.meta.ide.plugins.nothing

import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.resources.ArrowIcons
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.nothingIdePlugin: Plugin
  get() = "NothingIdePlugin" {
    meta(
      addLineMarkerProvider(
        icon = ArrowIcons.NOTHING,
        transform = {
          it.safeAs<KtUserType>()?.takeIf { type -> type.referencedName == "Nothing" }
            ?.referenceExpression?.getReferencedNameElement()
        },
        message = { "Bottom Type" }
      )
    )
  }
