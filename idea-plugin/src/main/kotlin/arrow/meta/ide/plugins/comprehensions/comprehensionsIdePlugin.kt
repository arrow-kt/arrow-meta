package arrow.meta.ide.plugins.comprehensions

import arrow.meta.Plugin
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.invoke
import arrow.meta.plugins.comprehensions.isBinding
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
val IdeMetaPlugin.comprehensionsIdePlugin: Plugin
  get() = "ComprehensionsIdePlugin" {
    meta(
      addLineMarkerProvider(
        icon = ArrowIcons.BIND,
        transform = { it.safeAs<KtExpression>()?.takeIf(KtExpression::isBinding)?.identifier },
        message = { "Bind" }
      )
    )
  }

private val KtExpression.identifier: PsiElement?
  get() = safeAs<KtProperty>()?.identifyingElement