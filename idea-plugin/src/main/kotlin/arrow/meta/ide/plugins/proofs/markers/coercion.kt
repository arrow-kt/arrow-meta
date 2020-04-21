package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.intentions.PairTypes
import arrow.meta.ide.plugins.proofs.intentions.explicitParticipatingTypes
import arrow.meta.ide.plugins.proofs.intentions.implicitParticipatingTypes
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import arrow.meta.plugins.proofs.phases.coerceProof
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import javax.swing.Icon

fun IdeMetaPlugin.coerceProofLineMarker(icon: Icon): ExtensionPhase =
  addLineMarkerProvider(
    icon = icon,
    transform = { psiElement ->
      psiElement.ctx()?.let { ctx ->
        psiElement.safeAs<KtElement>()?.takeIf {
          it.isCoerced(ctx)
        }
      }
    },
    message = { ktElement: KtElement ->
      ktElement.anyParticipatingTypes().mapNotNull { (subtype, supertype) ->
        ktElement.ctx()?.coerceProof(subtype, supertype)?.coercionMessage()
      }.firstOrNull() ?: "Proof not found"
    }
  )

fun KtElement.anyParticipatingTypes(): List<PairTypes> =
  (this.safeAs<KtCallElement>()?.explicitParticipatingTypes() ?: emptyList()) +
    (this.safeAs<KtProperty>()?.explicitParticipatingTypes() ?: emptyList()) +
    (this.safeAs<KtDotQualifiedExpression>()?.implicitParticipatingTypes() ?: emptyList())

private fun KtElement.isCoerced(compilerContext: CompilerContext): Boolean =
  anyParticipatingTypes().any { (subtype, supertype) ->
    compilerContext.areTypesCoerced(subtype, supertype)
  }