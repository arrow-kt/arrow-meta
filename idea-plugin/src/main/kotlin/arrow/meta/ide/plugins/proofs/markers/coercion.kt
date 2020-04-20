package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.intentions.PairTypes
import arrow.meta.ide.plugins.proofs.intentions.explicitParticipatingTypes
import arrow.meta.ide.plugins.proofs.intentions.implicitParticipatingTypes
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import arrow.meta.plugins.proofs.phases.coerceProof
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import javax.swing.Icon

fun IdeMetaPlugin.coerceProofLineMarker(icon: Icon): ExtensionPhase =
  addLineMarkerProvider(
    icon = icon,
    composite = KtProperty::class.java,
    transform = { psiElement ->
      psiElement.ctx()?.let { ctx ->
        psiElement.safeAs<KtProperty>()?.takeIf { it.isCoerced(ctx) }
      }
    },
    message = { ktProperty: KtElement ->
      ktProperty.anyParticipatingTypes().mapNotNull { (subtype, supertype) ->
        ktProperty.ctx()?.coerceProof(subtype, supertype)?.coercionMessage()
      }.firstOrNull() ?: "Proof not found"
    }
  )

private fun KtElement.anyParticipatingTypes(): List<PairTypes> =
  explicitParticipatingTypes() + implicitParticipatingTypes()

private fun KtElement.isCoerced(compilerContext: CompilerContext): Boolean {
  return anyParticipatingTypes().any { (subtype, supertype) ->
    compilerContext.areTypesCoerced(subtype, supertype)
  }
}