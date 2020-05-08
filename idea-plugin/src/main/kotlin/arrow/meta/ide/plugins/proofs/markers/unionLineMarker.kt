package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.folding.unionTypeMatches
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.phases.ExtensionPhase
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.unionTypeLineMarker: ExtensionPhase
  get() = addLineMarkerProviderM(
    icon = ArrowIcons.COPRODUCT, // replace with union icon
    composite = KtTypeReference::class.java,
    transform = { psiElement: PsiElement ->
      psiElement.safeAs<KtTypeReference>()?.takeIf { it.unionTypeMatches() }
    },
    message = {
      unionMessage() // improve message
    }
  )
