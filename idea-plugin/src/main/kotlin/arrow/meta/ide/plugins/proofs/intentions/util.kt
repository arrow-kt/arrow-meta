package arrow.meta.ide.plugins.proofs.intentions

import arrow.meta.ide.plugins.proofs.intentions.PairTypes.Companion.pairOrNull
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.debugger.sequence.psi.resolveType
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
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

fun KtElement.implicitParticipatingTypes(): List<PairTypes> =
  when (this) {
    is KtDotQualifiedExpression ->
      listOfNotNull(receiverExpression.resolveType() pairOrNull selectorExpression?.resolveType())
    else -> emptyList()
  }

fun KtElement.explicitParticipatingTypes(): List<PairTypes> =
  when (this) {
    is KtCallElement -> {
      // Obtain the argument types from the current call
      val subTypes = valueArgumentList?.arguments.orEmpty().mapNotNull { it.getArgumentExpression()?.resolveType() }

      val superTypes = analyze(bodyResolveMode = BodyResolveMode.FULL)
        .getSliceContents(BindingContext.RESOLVED_CALL)
        // get the calls for the current element
        .filter { (call, _) -> call.callElement == this }
        // then the argument types
        .entries.first().value.valueArguments
        .map { it.key.type }

      //TODO check for named and switched arguments

      subTypes.zip(superTypes, ::PairTypes)
    }

    is KtProperty -> {
      //TODO check destructuring
      val superType = type()
      val subType = initializer?.resolveType()
      listOfNotNull((subType pairOrNull superType))
    }

    else -> emptyList()
  }