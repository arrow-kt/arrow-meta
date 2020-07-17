package arrow.meta.ide.plugins.proofs

import arrow.meta.ide.dsl.utils.replaceK
import arrow.meta.ide.plugins.proofs.markers.description
import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.proofs.phases.CoercionProof
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import arrow.meta.plugins.proofs.phases.coerceProof
import arrow.meta.quotes.ktFile
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.imports.importableFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.resolve.ImportPath
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.KotlinType

internal fun KtDotQualifiedExpression.implicitParticipatingTypes(): Pair<KotlinType, KotlinType>? =
  receiverExpression.resolveKotlinType().pairOrNull(selectorExpression?.resolveKotlinType())

internal fun CompilerContext.coercionProofMessage(ktDotQualifiedExpression: KtDotQualifiedExpression): String =
  ktDotQualifiedExpression.implicitParticipatingTypes()?.let { (subtype, supertype) ->
    coerceProof(subtype, supertype)?.description()
  } ?: "Proof not found"

internal fun KotlinType?.pairOrNull(b: KotlinType?): Pair<KotlinType, KotlinType>? =
  if (this != null && b != null) Pair(this, b)
  else null

internal fun KtExpression.resolveKotlinType(): KotlinType? =
  analyze(BodyResolveMode.PARTIAL).getType(this)

internal fun CompilerContext.explicit(ktValueArgument: KtValueArgument): KtExpression? =
  // Get the coerced types (parameter type and actual definition type)
  ktValueArgument.participatingTypes()?.let { pairType ->
    ktValueArgument.getArgumentExpression()
      ?.takeIf { it.resolveKotlinType() == pairType.first }
      ?.let { replaceWithProof(it, pairType) }
  }

internal fun CompilerContext.explicit(ktProperty: KtProperty): KtExpression? =
  ktProperty.participatingTypes()?.let { pairType ->
    ktProperty.initializer?.let { replaceWithProof(it, pairType) }
  }

private fun CompilerContext.replaceWithProof(ktExpression: KtExpression, pairType: Pair<KotlinType, KotlinType>): KtExpression? =
  coerceProof(pairType.first, pairType.second)?.let { proof: CoercionProof ->
    // Add import (if needed)
    val importList: KtImportList? = ktExpression.containingKtFile.importList
    val importableFqName: FqName? = proof.through.importableFqName
    val throughPackage: FqName? = proof.through.ktFile()?.packageFqName

    val notImported: Boolean = importList?.let { ktImportList ->
      !ktImportList.imports.any { it.importedFqName == importableFqName }
    } ?: true
    val differentPackage: Boolean = ktExpression.containingKtFile.packageFqName != throughPackage

    if (notImported && differentPackage) {
      importableFqName?.let { fqName: FqName ->
        importDirective(ImportPath(fqName, false)).value?.let { importDirective: KtImportDirective ->
          importList?.add(importDirective as PsiElement)
        }
      }
    }

    // Replace with Proof
    "${ktExpression.text}.${proof.through.name}()".expression.value?.let { new: KtExpression ->
      ktExpression.replaceK(new)
    }
  }

internal fun KtElement.participatingTypes(): Pair<KotlinType, KotlinType>? =
  when (this) {
    is KtProperty -> participatingTypes()
    is KtValueArgument -> participatingTypes()
    else -> null
  }

internal fun CompilerContext?.isCoerced(ktElement: KtElement): Boolean =
  ktElement.participatingTypes()?.let { (subtype, supertype) ->
    this.areTypesCoerced(subtype, supertype)
  } ?: false

private fun KtProperty.participatingTypes(): Pair<KotlinType, KotlinType>? {
  val subType: KotlinType? = initializer?.resolveKotlinType()
  val superType: KotlinType? = type()
  return subType.pairOrNull(superType)
}

// TODO: can't resolve Pair<Type, Type> or other HKT * -> *
private fun KtValueArgument.participatingTypes(): Pair<KotlinType, KotlinType>? {
  val subType: KotlinType? = getArgumentExpression()?.resolveKotlinType()

  val ktCallExpression: KtCallExpression? = PsiTreeUtil.getParentOfType(this, KtCallExpression::class.java)
  val myselfIndex: Int = ktCallExpression?.valueArguments?.indexOf(this) ?: 0
  val superType: KotlinType? = ktCallExpression.getResolvedCall(analyze())?.let { resolvedCall: ResolvedCall<out CallableDescriptor> ->
    resolvedCall.resultingDescriptor.valueParameters.getOrNull(myselfIndex)?.type
  }
  return subType.pairOrNull(superType)
}