package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.coerceProof
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.implicitCoercionValueArgumentLineMarker: ExtensionPhase
  get() = addLineMarkerProviderMNotLeaf(
    icon = ArrowIcons.ICON4,
    notALeafComposite = KtValueArgument::class.java,
    transform = { psiElement: PsiElement ->
      psiElement.ctx()?.let { ctx ->
        psiElement.safeAs<KtValueArgument>()?.takeIf {
          it.isCoerced(ctx)
        }
      }
    },
    message = { ktElement: KtValueArgument ->
      ktElement.participatingTypes()?.let { (subtype, supertype) ->
        ktElement.ctx()?.coerceProof(subtype, supertype)?.coercionMessage()
      } ?: "Proof not found"
    }
  )
