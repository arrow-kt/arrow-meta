package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.intentions.isCoerced
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
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