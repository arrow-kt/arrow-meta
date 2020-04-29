package arrow.meta.ide.plugins.proofs.inspections

import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.KotlinType

internal fun KotlinType?.pairOrNull(b: KotlinType?): Pair<KotlinType, KotlinType>? =
  if (this != null && b != null) Pair(this, b)
  else null

internal fun KtExpression.resolveKotlinType(): KotlinType? =
  analyze(BodyResolveMode.PARTIAL).getType(this)
