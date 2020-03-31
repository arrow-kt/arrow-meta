package arrow.meta.ide.plugins.proofs.intentions

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.intentions.PairTypes.Companion.pairOrNull
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import org.jetbrains.kotlin.idea.debugger.sequence.psi.resolveType
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement

fun IdeMetaPlugin.makeImplicitCoercionIntention(compilerContext: CompilerContext): ExtensionPhase =
  addIntention(
    text = "Make coercion implicit",
    kClass = KtElement::class.java,
    isApplicableTo = { ktCall: KtElement, _ ->
      ktCall.implicitParticipatingTypes().any { (subtype, supertype) ->
        compilerContext.areTypesCoerced(subtype, supertype)
      }
    },
    applyTo = { ktCall: KtElement, _ ->
      when (ktCall) {
        is KtDotQualifiedExpression -> {
          ktCall.replace(ktCall.receiverExpression)
        }
      }
    }
  )

fun KtElement.implicitParticipatingTypes(): List<PairTypes> =
  when (this) {

    is KtDotQualifiedExpression ->
      (receiverExpression.resolveType() pairOrNull selectorExpression?.resolveType())?.let(::listOf)
        ?: emptyList()

    else -> emptyList()
  }
