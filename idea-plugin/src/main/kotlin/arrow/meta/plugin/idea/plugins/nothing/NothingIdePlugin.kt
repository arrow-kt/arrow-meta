package arrow.meta.plugin.idea.plugins.nothing

import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.plugin.idea.IdeMetaPlugin
import arrow.meta.plugin.idea.resources.ArrowIcons
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.nothingIdePlugin: Plugin
  get() = "NothingIdePlugin" {
    meta(
      addLineMarkerProvider(
        icon = ArrowIcons.NOTHING,
        matchOn = { it.safeAs<KtUserType>()?.takeIf { type -> type.referencedName == "Nothing" } },
        message = { "Bottom Type" }
      )
    )
  }
