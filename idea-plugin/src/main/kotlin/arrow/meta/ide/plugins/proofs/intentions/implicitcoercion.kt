package arrow.meta.ide.plugins.proofs.intentions

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.resolve.baseLineTypeChecker
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import arrow.meta.plugins.proofs.phases.coerceProof
import org.jetbrains.kotlin.idea.debugger.sequence.psi.resolveType
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.types.KotlinType

fun IdeMetaPlugin.makeImplicitCoercionIntention(compilerContext: CompilerContext): ExtensionPhase =
  addIntention(
    text = "Make coercion implicit",
    kClass = KtElement::class.java,
    isApplicableTo = { ktCall: KtElement, _ ->
      ktCall.participatingTypes()?.let { (subtype, supertype) ->
        compilerContext.areTypesCoerced(subtype, supertype)
      } ?: false
    },
    applyTo = { ktCall: KtElement, _ ->
      when (ktCall) {
        is KtDotQualifiedExpression -> {
          ktCall.replace(ktCall.receiverExpression)
        }
      }
    }
  )

private fun KtElement.participatingTypes(): Pair<KotlinType, KotlinType>? =
  when (this) {
    is KtDotQualifiedExpression ->
      selectorExpression?.let { receiverExpression.resolveType() to it.resolveType() }
    else -> null
  }
