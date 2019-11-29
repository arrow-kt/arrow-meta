package arrow.meta.ide.plugins.comprehensions

import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.editor.lineMarker.addLineMarkerProvider
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.plugins.comprehensions.isBinding
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtProperty
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
val IdeMetaPlugin.comprehensionsIdePlugin: Plugin
  get() = "ComprehensionsIdePlugin" {
    meta(
      addLineMarkerProvider<KtProperty>(
        icon = ArrowIcons.BIND,
        transform = { it.takeIf(KtExpression::isBinding) },
        message = { "Bind" }
      )
    )
  }