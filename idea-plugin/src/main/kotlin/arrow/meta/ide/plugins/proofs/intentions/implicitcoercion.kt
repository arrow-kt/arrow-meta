package arrow.meta.ide.plugins.proofs.intentions

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import org.jetbrains.kotlin.idea.debugger.sequence.psi.resolveType
import org.jetbrains.kotlin.idea.references.resolveMainReferenceToDescriptors
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.types.KotlinType

fun IdeMetaPlugin.makeImplicitCoercionIntention(compilerContext: CompilerContext): ExtensionPhase =
  addIntention(
    text = "Make coercion implicit",
    kClass = KtElement::class.java,
    isApplicableTo = { ktCall: KtElement, _ ->
      ktCall.implicitParticipatingTypes()?.let { (subtype, supertype) ->
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

fun KtElement.implicitParticipatingTypes(): Pair<KotlinType, KotlinType>? =
  when (this) {
    is KtCallElement -> {
//      //TODO implement for multiple args
//      val subType = valueArgumentList?.arguments?.get(0)?.getArgumentExpression()?.resolveType()
//      val superType = TODO() // extract expected argument types
//      subType toOrNull superType
      null
    }

    is KtProperty -> {
      val superType = type()
      val subType = initializer?.resolveType()
      subType toOrNull superType

    }

    else -> null
  }