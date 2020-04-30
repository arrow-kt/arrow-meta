package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.inspections.pairOrNull
import arrow.meta.ide.plugins.proofs.inspections.resolveKotlinType
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.types.KotlinType

val IdeMetaPlugin.coercionCallSiteLineMarker: ExtensionPhase
  get() = Composite(
    implicitCoercionValueArgumentLineMarker,
    implicitCoercionPropertyLineMarker
  )

internal fun KtElement.participatingTypes(): Pair<KotlinType, KotlinType>? =
  when (this) {
    is KtProperty -> participatingTypes()
    is KtValueArgument -> participatingTypes()
    else -> null
  }

internal fun CompilerContext.isCoerced(ktElement: KtElement): Boolean =
  ktElement.participatingTypes()?.let { (subtype, supertype) ->
    areTypesCoerced(subtype, supertype)
  } ?: false

private fun KtProperty.participatingTypes(): Pair<KotlinType, KotlinType>? {
  val subType = initializer?.resolveKotlinType()
  val superType = type()
  return subType.pairOrNull(superType)
}

private fun KtValueArgument.participatingTypes(): Pair<KotlinType, KotlinType>? {
  val subType: KotlinType? = getArgumentExpression()?.resolveKotlinType()

  val ktCallExpression = PsiTreeUtil.getParentOfType(this, KtCallExpression::class.java)
  val myselfIndex = ktCallExpression?.valueArguments?.indexOf(this) ?: 0
  val superType = ktCallExpression.getResolvedCall(analyze())?.let { resolvedCall ->
    resolvedCall.resultingDescriptor.valueParameters[myselfIndex].type
  }

  return subType.pairOrNull(superType)
}
