package arrow.meta.ide.plugins.proofs.intentions

import arrow.meta.ide.plugins.proofs.intentions.PairTypes.Companion.pairOrNull
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
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

//TODO check for named and switched arguments
//TODO check destructuring
fun KtElement.explicitParticipatingTypes(): List<PairTypes> =
  when (this) {
    is KtValueArgument -> {
      val subType: KotlinType? = getArgumentExpression()?.resolveKotlinType()

      val ktCallExpression = PsiTreeUtil.getParentOfType(this, KtCallExpression::class.java)
      val myselfIndex = ktCallExpression?.valueArguments?.indexOf(this) ?: 0
      val resolvedCall = ktCallExpression.getResolvedCall(analyze())
      val superType5 = resolvedCall?.let {
        resolvedCall.resultingDescriptor.valueParameters[myselfIndex].type
      }

      listOfNotNull((subType pairOrNull superType5))
    }

    is KtProperty -> {
      val subType = initializer?.resolveKotlinType()
      val superType = type()
      listOfNotNull((subType pairOrNull superType))
    }

    else -> emptyList()
  }

fun KtExpression.resolveKotlinType(): KotlinType? =
  analyze(BodyResolveMode.PARTIAL).getType(this)
