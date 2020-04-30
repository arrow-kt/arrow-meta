package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.coerceProof
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.implicitCoercionPropertyLineMarker: ExtensionPhase
  get() = addLineMarkerProvider(
    icon = ArrowIcons.ICON4,
    composite = KtProperty::class.java,
    transform = { psiElement ->
      psiElement.ctx()?.let { ctx ->
        psiElement.safeAs<KtProperty>()?.takeIf {
          it.isCoerced(ctx)
        }
      }
    },
    message = { ktElement: KtProperty ->
      ktElement.participatingTypes()?.let { (subtype, supertype) ->
        ktElement.ctx()?.coerceProof(subtype, supertype)?.coercionMessage()
      } ?: "Proof not found"
    }
  )
