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
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.implicitCoercionValueArgumentLineMarker: ExtensionPhase
  get() = addLineMarkerProviderM(
    icon = ArrowIcons.ICON4,
    transform = { psiElement: PsiElement ->
      psiElement.ctx()?.let { ctx ->
        psiElement.safeAs<KtValueArgument>()?.takeIf {
          it.isCoerced(ctx)
        }
      }
    },
    message = { ktElement: KtValueArgument ->
      ktElement.participatingTypes().mapNotNull { (subtype, supertype) ->
        ktElement.ctx()?.coerceProof(subtype, supertype)?.coercionMessage()
      }.firstOrNull() ?: "Proof not found"
    }
  )

fun KtValueArgument.participatingTypes(): List<PairTypes> {
  val subType: KotlinType? = getArgumentExpression()?.resolveKotlinType()

  val ktCallExpression = PsiTreeUtil.getParentOfType(this, KtCallExpression::class.java)
  val myselfIndex = ktCallExpression?.valueArguments?.indexOf(this) ?: 0
  val resolvedCall = ktCallExpression.getResolvedCall(analyze())
  val superType5 = resolvedCall?.let {
    resolvedCall.resultingDescriptor.valueParameters[myselfIndex].type
  }

  return listOfNotNull((subType pairOrNull superType5))
}

private fun KtValueArgument.isCoerced(compilerContext: CompilerContext): Boolean =
  participatingTypes().any { (subtype, supertype) ->
    compilerContext.areTypesCoerced(subtype, supertype)
  }
