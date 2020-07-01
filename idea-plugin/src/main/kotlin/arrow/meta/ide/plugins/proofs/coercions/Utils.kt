package arrow.meta.ide.plugins.proofs.coercions

import arrow.meta.ide.dsl.utils.replaceK
import arrow.meta.ide.plugins.proofs.markers.description
import arrow.meta.ide.plugins.proofs.markers.markerMessage
import arrow.meta.ide.plugins.proofs.markers.participatingTypes
import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.proofs.phases.CoercionProof
import arrow.meta.plugins.proofs.phases.coerceProof
import arrow.meta.quotes.ktFile
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.imports.importableFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.resolve.ImportPath
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