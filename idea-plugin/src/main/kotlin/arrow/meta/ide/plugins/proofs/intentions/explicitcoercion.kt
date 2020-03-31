package arrow.meta.ide.plugins.proofs.intentions

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.intentions.PairTypes.Companion.pairOrNull
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import arrow.meta.plugins.proofs.phases.coerceProof
import org.jetbrains.kotlin.idea.debugger.sequence.psi.resolveType
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.KotlinType

fun IdeMetaPlugin.makeExplicitCoercionIntention(compilerContext: CompilerContext): ExtensionPhase =
  addIntention(
    text = "Make coercion explicit",
    kClass = KtElement::class.java,
    isApplicableTo = { ktCall: KtElement, _ ->
      ktCall.explicitParticipatingTypes().any { (subtype, supertype) ->
        compilerContext.areTypesCoerced(subtype, supertype)
      }
    },
    applyTo = { ktCall: KtElement, _ ->
      when (ktCall) {
        is KtProperty -> {
          ktCall.explicitParticipatingTypes()?.let { (subtype, supertype) ->
            ktCall.initializer?.let { initializer ->
              initializer.replace(createExpression(
                "${initializer.text}.${compilerContext.coerceProof(subtype, supertype)?.through?.name}()"))
            }
          }
        }
      }
    }
  )


//TODO move elsewhere
data class PairTypes(val subType: KotlinType, val superType: KotlinType) {
  companion object {
    infix fun KotlinType?.pairOrNull(b: KotlinType?): PairTypes? =
      if (this != null && b != null) PairTypes(this, b)
      else null
  }
}

fun KtElement.explicitParticipatingTypes(): List<PairTypes> =
  when (this) {
    is KtCallElement -> {
      // Obtain the argument types from the current call
      val subTypes = valueArgumentList?.arguments?.mapNotNull { it.getArgumentExpression()?.resolveType() }
        ?: emptyList()

      val superTypes = analyze(bodyResolveMode = BodyResolveMode.FULL)
        .getSliceContents(BindingContext.RESOLVED_CALL)
        // get the calls for the current element
        .filter { (call, _) -> call.callElement == this }
        // then the argument types
        .entries.first().value.valueArguments
        .map { it.key.type }

      //TODO check for named and switched arguments!

      subTypes.zip(superTypes, ::PairTypes)
    }

    is KtProperty -> {
      val superType = type()
      val subType = initializer?.resolveType()
      (subType pairOrNull superType)?.let(::listOf) ?: emptyList()
    }
    else -> emptyList()
  }
