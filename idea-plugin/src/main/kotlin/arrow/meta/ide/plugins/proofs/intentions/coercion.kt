package arrow.meta.ide.plugins.proofs.intentions

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.resolve.baseLineTypeChecker
import arrow.meta.plugins.proofs.phases.coerceProof
import org.jetbrains.kotlin.idea.debugger.sequence.psi.resolveType
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.types.KotlinType

fun IdeMetaPlugin.makeExplicitCoercionIntention(compilerContext: CompilerContext): ExtensionPhase =
  addIntention(
    text = "Make coercion explicit",
    kClass = KtElement::class.java,
    isApplicableTo = { ktCall: KtElement, caretOffset: Int ->
      ktCall.participatingTypes()?.let { (subtype, supertype) ->
        compilerContext.areTypesCoerced(subtype, supertype)
      } ?: false
    },
    applyTo = { ktCall: KtElement, editor ->
      TODO()
    }
  )

fun IdeMetaPlugin.makeImplicitCoercionIntention(compilerContext: CompilerContext): ExtensionPhase =
  addIntention(
    text = "Make coercion implicit",
    kClass = KtElement::class.java,
    isApplicableTo = { ktCall: KtElement, caretOffset: Int ->
      ktCall.participatingTypes()?.let { (subtype, supertype) ->
        compilerContext.areTypesCoerced(subtype, supertype)
      } ?: false
    },
    applyTo = { ktCall: KtElement, editor ->
      when (ktCall) {
        is KtDotQualifiedExpression -> {
          ktCall.replace(ktCall.receiverExpression)
        }
        else -> false
      }
    }
  )

private fun KtElement.participatingTypes(): Pair<KotlinType, KotlinType>? =
  when (this) {
    is KtDotQualifiedExpression ->
      selectorExpression?.let { receiverExpression.resolveType() to it.resolveType() }

    is KtProperty -> {
      val subtype = type()
      val supertype = initializer?.resolveType()
      if (subtype != null && supertype != null) subtype to supertype
      else null
    }

    else -> null
  }

private fun CompilerContext.areTypesCoerced(subtype: KotlinType, supertype: KotlinType): Boolean {
  val isSubtypeOf = baseLineTypeChecker.isSubtypeOf(subtype, supertype)

  return if (!isSubtypeOf) {
    val isProofSubtype = ctx.coerceProof(subtype, supertype) != null

    !isSubtypeOf && isProofSubtype

  } else false
}