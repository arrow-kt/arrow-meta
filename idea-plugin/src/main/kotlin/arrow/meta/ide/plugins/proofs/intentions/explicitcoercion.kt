package arrow.meta.ide.plugins.proofs.intentions

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import org.jetbrains.kotlin.idea.debugger.sequence.psi.resolveType
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.types.KotlinType

fun IdeMetaPlugin.makeExplicitCoercionIntention(compilerContext: CompilerContext): ExtensionPhase =
  addIntention(
    text = "Make coercion explicit",
    kClass = KtElement::class.java,
    isApplicableTo = { ktCall: KtElement, _ ->
      ktCall.participatingTypes()?.let { (subtype, supertype) ->
        compilerContext.areTypesCoerced(subtype, supertype)
      } ?: false
    },
    applyTo = { ktCall: KtElement, _ ->
      TODO()
    }
  )

//TODO move elsewhere
fun KtElement.participatingTypes(): Pair<KotlinType, KotlinType>? =
  when (this) {
    is KtCallElement -> {
      //TODO implement for multiple args
      val subType = valueArgumentList?.arguments?.get(0)?.getArgumentExpression()?.resolveType()
      val superType = TODO() // extract expected argument types
      subType toOrNull superType
    }

    is KtDotQualifiedExpression ->
      receiverExpression.resolveType() toOrNull selectorExpression?.resolveType()

    is KtProperty -> {
      val superType = type()
      val subType = initializer?.resolveType()
      subType toOrNull superType

    }

    else -> null
  }

private infix fun <A, B> A?.toOrNull(b: B?): Pair<A, B>? =
  if (this != null && b != null) this to b
  else null