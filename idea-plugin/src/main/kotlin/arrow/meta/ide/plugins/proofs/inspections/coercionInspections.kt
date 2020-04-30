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
import org.jetbrains.kotlin.psi.KtImportDirective
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

internal fun KtElement.makeExplicit(compilerContext: CompilerContext) =
  when (this) {
    is KtProperty -> makeExplicit(compilerContext)
    is KtValueArgument -> makeExplicit(compilerContext)
    else -> {
    }
  }

private fun KtValueArgument.makeExplicit(compilerContext: CompilerContext) {
  // Get the coerced types (parameter type and actual definition type)
  participatingTypes()?.let { pairType ->
    getArgumentExpression()?.let { ktExpression ->
      val type = ktExpression.resolveKotlinType()
      if (pairType.first == type) {
        ktExpression.replaceWithProof(compilerContext, pairType)
      }
    }
  }
}

private fun KtProperty.makeExplicit(compilerContext: CompilerContext) {
  participatingTypes()?.let { pairType ->
    initializer?.replaceWithProof(compilerContext, pairType)
  }
}

private fun KtExpression.replaceWithProof(compilerContext: CompilerContext, pairType: Pair<KotlinType, KotlinType>) = with(compilerContext) {
  val through = coerceProof(pairType.first, pairType.second)!!.through
  val importList = containingKtFile.importList!!
  val importableFqName = through.importableFqName
  val throughPackage = through.ktFile()?.packageFqName

  val notImported = !importList.imports.any { it.importedFqName == importableFqName }
  val differentPackage = containingKtFile.packageFqName != throughPackage

  if (notImported && differentPackage) {
    val proofImport = importableFqName?.let {
      importDirective(ImportPath(it, false)).value
    }
    proofImport?.let { importDirective: KtImportDirective ->
      importList.add(importDirective as PsiElement)
    }
  }
  val ktExpression: KtExpression? = "$text.${through.name}()".expression.value
  ktExpression?.let(::replace)
}
