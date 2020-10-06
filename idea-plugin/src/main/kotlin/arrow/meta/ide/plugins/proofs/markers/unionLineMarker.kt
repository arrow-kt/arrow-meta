package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.folding.unionTypeMatches
import arrow.meta.ide.plugins.proofs.utils.addLineMarkerProviderM
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.phases.ExtensionPhase
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

// Not needed for now, with the foldingBuilder its enough
val IdeMetaPlugin.unionTypeLineMarker: ExtensionPhase
  get() = addLineMarkerProviderM(
    icon = ArrowIcons.UNION,
    composite = KtTypeReference::class.java,
    transform = { psiElement: PsiElement ->
      psiElement.safeAs<KtTypeReference>()?.takeIf { it.unionTypeMatches() }
    },
    message = {
      unionMessage()
    }
  )

private fun unionMessage(): String =
  """
    Union type
  """.trimIndent()
