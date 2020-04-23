package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.intentions.PairTypes
import arrow.meta.ide.plugins.proofs.intentions.explicitParticipatingTypes
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import arrow.meta.plugins.proofs.phases.coerceProof
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import javax.swing.Icon

fun IdeMetaPlugin.coerceProofLineMarker(icon: Icon): ExtensionPhase =
  addLineMarkerProvider(
    icon = icon,
    composite = KtProperty::class.java,
    transform = { psiElement: PsiElement ->
      psiElement.ctx()?.let { ctx ->
        psiElement.safeAs<KtProperty>()?.takeIf {
          it.isCoerced(ctx)
        }
      }
    },
    message = { ktElement: KtProperty ->
      ktElement.anyParticipatingTypes().mapNotNull { (subtype, supertype) ->
        ktElement.ctx()?.coerceProof(subtype, supertype)?.coercionMessage()
      }.firstOrNull() ?: "Proof not found"
    }
  )

fun KtElement.anyParticipatingTypes(): List<PairTypes> =
  (this.safeAs<KtValueArgument>()?.explicitParticipatingTypes() ?: emptyList()) +
    (this.safeAs<KtProperty>()?.explicitParticipatingTypes() ?: emptyList())

private fun KtElement.isCoerced(compilerContext: CompilerContext): Boolean =
  anyParticipatingTypes().any { (subtype, supertype) ->
    compilerContext.areTypesCoerced(subtype, supertype)
  }