package arrow.meta.plugin.idea.plugins.nothing

import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.plugin.idea.IdeMetaPlugin
import arrow.meta.plugin.idea.resources.ArrowIcons
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.nothingIdePlugin: Plugin
  get() = "NothingIdePlugin" {
    meta(
      addLineMarkerProvider(
        icon = ArrowIcons.NOTHING,
        message = { "Bottom Type" }
      ) { userType: PsiElement -> userType.safeAs<KtUserType>()?.referencedName == "Nothing" }
    )
  }
