package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.coerceProof
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.implicitCoercionValueArgumentLineMarker: ExtensionPhase
  get() = addLineMarkerProviderM(
    icon = ArrowIcons.ICON4,
    composite = KtValueArgument::class.java,
    transform = { psiElement: PsiElement ->
      psiElement.safeAs<KtValueArgument>()?.takeIf {
        psiElement.ctx().isCoerced(it)
      }
    },
    message = { ktElement: KtValueArgument ->
      ktElement.participatingTypes()?.let { (subtype, supertype) ->
        ktElement.ctx()?.coerceProof(subtype, supertype)?.description()
      } ?: "Proof not found"
    }
  )
