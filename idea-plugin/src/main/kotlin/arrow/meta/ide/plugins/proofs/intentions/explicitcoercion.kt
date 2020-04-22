package arrow.meta.ide.plugins.proofs.intentions

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import arrow.meta.plugins.proofs.phases.coerceProof
import arrow.meta.quotes.ktFile
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.imports.importableFqName
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.resolve.ImportPath
import org.jetbrains.kotlin.types.KotlinType

/**
 * [explicitCoercionIntention]: for implicit coercion to make it explicit
 */
val IdeMetaPlugin.explicitCoercionIntention: ExtensionPhase
  get() = addApplicableInspection(
    defaultFixText = "Make_coercion_explicit",
    enabledByDefault = false,
    kClass = KtElement::class.java,
    isApplicable = { ktCall: KtElement ->
      ktCall.ctx()?.let { compilerContext ->
        ktCall.explicitParticipatingTypes().any { (subtype, supertype) ->
          compilerContext.areTypesCoerced(subtype, supertype)
        }
      } ?: false
    },
    applyTo = { ktCall: KtElement, _, _ ->
      ktCall.ctx()?.let { compilerContext ->
        ktCall.makeExplicit(compilerContext)
      }
    },
    inspectionText = { "Not used at the moment because the highlight type used is ProblemHighlightType.INFORMATION" },
    inspectionHighlightType = { ProblemHighlightType.INFORMATION },
    groupPath = ProofPath + arrayOf("Coercion")
  )

private fun KtElement.makeExplicit(compilerContext: CompilerContext) {
  when (this) {
    is KtValueArgument -> {
      // Get the coerced types (parameter type and actual definition type)
      explicitParticipatingTypes().firstOrNull { (subtype: KotlinType, supertype: KotlinType) ->
        compilerContext.areTypesCoerced(subtype, supertype)
      }?.let { pairType: PairTypes ->
        getArgumentExpression()?.let { ktExpression ->
          val type = ktExpression.resolveKotlinType()
          if (pairType.subType == type) {
            ktExpression.replaceWithProof(compilerContext, pairType)
          }
        }
      }
    }

    is KtProperty -> explicitParticipatingTypes().first().let { pairType ->
      initializer?.replaceWithProof(compilerContext, pairType)
    }
  }
}

private fun KtExpression.replaceWithProof(compilerContext: CompilerContext, pairType: PairTypes) = with(compilerContext) {
  val through = coerceProof(pairType.subType, pairType.superType)!!.through
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
      importList.add(importDirective as PsiElement)// TODO sort?
    }
  }
  val ktExpression: KtExpression? = "$text.${through.name}()".expression.value
  ktExpression?.let(::replace)
}
