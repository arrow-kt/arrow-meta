package arrow.meta.ide.plugins.proofs.intentions

import arrow.meta.ide.plugins.proofs.intentions.PairTypes.Companion.pairOrNull
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.KotlinType

data class PairTypes(val subType: KotlinType, val superType: KotlinType) {
  companion object {
    infix fun KotlinType?.pairOrNull(b: KotlinType?): PairTypes? =
      if (this != null && b != null) PairTypes(this, b)
      else null
  }
}

fun KtDotQualifiedExpression.implicitParticipatingTypes(): List<PairTypes> =
  listOfNotNull(receiverExpression.resolveKotlinType() pairOrNull selectorExpression?.resolveKotlinType())

fun KtElement.explicitParticipatingTypes(): List<PairTypes> =
  when (this) {
    is KtCallElement -> {
      // Obtain the argument types from the current call
      val subTypes: List<KotlinType> = valueArgumentList?.arguments
        .orEmpty()
        .mapNotNull { it.getArgumentExpression()?.resolveKotlinType() }

      val superTypes: List<KotlinType> = analyze(bodyResolveMode = BodyResolveMode.FULL)
        .getSliceContents(BindingContext.RESOLVED_CALL)
        // get the calls for the current element
        .filter { (call, _) -> call.callElement == this }
        // then the argument types
        .entries.firstOrNull()?.value?.valueArguments
        ?.map { it.key.type } ?: emptyList()

      //TODO check for named and switched arguments

      subTypes.zip(superTypes, ::PairTypes)
    }

    is KtProperty -> {
      //TODO check destructuring
      val subType = initializer?.resolveKotlinType()
      val superType = type()
      listOfNotNull((subType pairOrNull superType))
    }

    else -> emptyList()
  }

fun KtExpression.resolveKotlinType(): KotlinType? =
  analyze(BodyResolveMode.PARTIAL).getType(this)
