package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.intentions.participatingTypes
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import javax.swing.Icon

fun IdeMetaPlugin.coerceProofLineMarker(icon: Icon, compilerContext: CompilerContext): ExtensionPhase =
  addLineMarkerProvider(
    icon = icon,
    composite = KtProperty::class.java,
    transform = { psiElement ->
      psiElement.safeAs<KtProperty>()?.takeIf { it.isCoerced(compilerContext) }
    },
    message = { "Coercion happening by proof" }
  )

private fun KtElement.isCoerced(compilerContext: CompilerContext): Boolean {
  return participatingTypes()?.let { (subtype, supertype) ->
    compilerContext.areTypesCoerced(subtype, supertype)
  } ?: false
}