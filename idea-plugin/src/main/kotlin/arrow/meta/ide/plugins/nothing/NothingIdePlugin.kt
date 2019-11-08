package arrow.meta.ide.plugins.nothing

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.IdePlugin
import arrow.meta.ide.invoke
import arrow.meta.ide.plugins.comprehensions.identifier
import arrow.meta.ide.resources.ArrowIcons
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.nothingIdePlugin: IdePlugin
  get() = "NothingIdePlugin" {
    meta(
      addLineMarkerProvider(
        icon = ArrowIcons.NOTHING,
        transform = { it.safeAs<KtUserType>()?.takeIf { type -> type.referencedName == "Nothing" }
          ?.referenceExpression?.getReferencedNameElement()},
        message = { "Bottom Type" }
      )
    )
  }
