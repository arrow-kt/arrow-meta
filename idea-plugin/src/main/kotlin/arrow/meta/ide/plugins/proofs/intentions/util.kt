package arrow.meta.ide.plugins.proofs.intentions

import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.KotlinType

data class PairTypes(val subType: KotlinType, val superType: KotlinType) {
  companion object {
    infix fun KotlinType?.pairOrNull(b: KotlinType?): PairTypes? =
      if (this != null && b != null) PairTypes(this, b)
      else null
  }
}

//TODO check for named and switched arguments
//TODO check destructuring

fun KtExpression.resolveKotlinType(): KotlinType? =
  analyze(BodyResolveMode.PARTIAL).getType(this)