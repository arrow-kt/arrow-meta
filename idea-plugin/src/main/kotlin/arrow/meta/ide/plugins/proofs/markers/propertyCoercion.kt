package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.inspections.PairTypes
import arrow.meta.ide.plugins.proofs.inspections.PairTypes.Companion.pairOrNull
import arrow.meta.ide.plugins.proofs.inspections.resolveKotlinType
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import arrow.meta.plugins.proofs.phases.coerceProof
import org.jetbrains.kotlin.nj2k.postProcessing.type
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
      ktElement.participatingTypes().mapNotNull { (subtype, supertype) ->
        ktElement.ctx()?.coerceProof(subtype, supertype)?.coercionMessage()
      }.firstOrNull() ?: "Proof not found"
    }
  )

internal fun KtProperty.participatingTypes(): List<PairTypes> {
  val subType = initializer?.resolveKotlinType()
  val superType = type()
  return listOfNotNull((subType pairOrNull superType))
}

private fun KtProperty.isCoerced(compilerContext: CompilerContext): Boolean =
  participatingTypes().any { (subtype, supertype) ->
    compilerContext.areTypesCoerced(subtype, supertype)
  }
