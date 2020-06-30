package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.coerceProof
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.implicitCoercionPropertyLineMarker: ExtensionPhase
  get() = addLineMarkerProvider(
    icon = ArrowIcons.ICON4,
    composite = KtProperty::class.java,
    transform = { psiElement: PsiElement ->
      psiElement.safeAs<KtProperty>()?.takeIf {
        psiElement.ctx().isCoerced(it)
      }
    },
    message = { ktElement: KtProperty ->
      ktElement.participatingTypes()?.let { (subtype, supertype) ->
        ktElement.ctx()?.coerceProof(subtype, supertype)?.description()
      } ?: "Proof not found"
    }
  )
