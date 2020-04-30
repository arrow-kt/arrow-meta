package arrow.meta.ide.plugins.proofs.inspections

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.markers.coercionMessage
import arrow.meta.ide.plugins.proofs.markers.participatingTypes
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.coerceProof
import arrow.meta.quotes.ktFile
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.imports.importableFqName
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.resolve.ImportPath
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.KotlinType

val IdeMetaPlugin.coercionInspections: ExtensionPhase
  get() = Composite(
    implicitCoercionInspection,
    explicitCoercionArgumentInspection,
    explicitCoercionPropertyInspection
  )

internal fun KtDotQualifiedExpression.implicitParticipatingTypes(): Pair<KotlinType, KotlinType>? =
  receiverExpression.resolveKotlinType().pairOrNull(selectorExpression?.resolveKotlinType())

internal fun KtDotQualifiedExpression.coercionProofMessage(ctx: CompilerContext): String =
  implicitParticipatingTypes()?.let { (subtype, supertype) ->
    ctx.coerceProof(subtype, supertype)?.coercionMessage()
  } ?: "Proof not found"

internal fun KotlinType?.pairOrNull(b: KotlinType?): Pair<KotlinType, KotlinType>? =
  if (this != null && b != null) Pair(this, b)
  else null

internal fun KtExpression.resolveKotlinType(): KotlinType? =
  analyze(BodyResolveMode.PARTIAL).getType(this)

internal fun KtElement.explicit(compilerContext: CompilerContext) =
  when (this) {
    is KtProperty -> explicit(compilerContext)
    is KtValueArgument -> explicit(compilerContext)
    else -> Unit
  }

private fun KtValueArgument.explicit(compilerContext: CompilerContext) {
  // Get the coerced types (parameter type and actual definition type)
  participatingTypes()?.let { pairType ->
    getArgumentExpression()?.let { ktExpression ->
      val type = ktExpression.resolveKotlinType()
      if (pairType.first == type) {
        compilerContext.replaceWithProof(ktExpression, pairType)
      }
    }
  }
}

private fun KtProperty.explicit(compilerContext: CompilerContext) {
  participatingTypes()?.let { pairType ->
    initializer?.let { compilerContext.replaceWithProof(it, pairType) }
  }
}

private fun CompilerContext.replaceWithProof(ktExpression: KtExpression, pairType: Pair<KotlinType, KotlinType>) {
  coerceProof(pairType.first, pairType.second)?.let { proof ->
    // Add import (if needed)
    val importList = ktExpression.containingKtFile.importList
    val importableFqName = proof.through.importableFqName
    val throughPackage = proof.through.ktFile()?.packageFqName

    val notImported = importList?.let { ktImportList ->
      !ktImportList.imports.any { it.importedFqName == importableFqName }
    } ?: true
    val differentPackage = ktExpression.containingKtFile.packageFqName != throughPackage

    if (notImported && differentPackage) {
      importableFqName?.let {
        importDirective(ImportPath(it, false)).value?.let { importDirective ->
          importList?.add(importDirective as PsiElement)
        }
      }
    }

    // Replace with Proof
    val ktExpressionNew: KtExpression? = "${ktExpression.text}.${proof.through.name}()".expression.value
    ktExpressionNew?.let { ktExpression.replace(it) }
  }
}
