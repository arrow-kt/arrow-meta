package arrow.meta.ide.plugins.proofs.inspections

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.IdeSyntax
import arrow.meta.ide.plugins.proofs.markers.participatingTypes
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import arrow.meta.plugins.proofs.phases.coerceProof
import arrow.meta.quotes.ktFile
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.imports.importableFqName
import org.jetbrains.kotlin.idea.inspections.AbstractApplicabilityBasedInspection
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.resolve.ImportPath
import org.jetbrains.kotlin.types.KotlinType

/**
 * [explicitCoercionArgumentInspection]: for implicit coercion on arguments to make them explicit
 */
val IdeMetaPlugin.explicitCoercionArgumentInspection: ExtensionPhase
  get() = addLocalInspection(
    inspection = explicitCoercionKtValueArgument,
    level = HighlightDisplayLevel.WEAK_WARNING,
    groupPath = ProofPath + arrayOf("Coercion")
  )

/**
 * [explicitCoercionPropertyInspection]: for implicit coercion on properties to make them explicit
 */
val IdeMetaPlugin.explicitCoercionPropertyInspection: ExtensionPhase
  get() = addLocalInspection(
    inspection = explicitCoercionKtProperty,
    level = HighlightDisplayLevel.WEAK_WARNING,
    groupPath = ProofPath + arrayOf("Coercion")
  )

const val COERCION_EXPLICIT_ARGS = "Coercion_explicit_args"

val IdeSyntax.explicitCoercionKtValueArgument: AbstractApplicabilityBasedInspection<KtValueArgument>
  get() = applicableInspection(
    defaultFixText = COERCION_EXPLICIT_ARGS,
    inspectionHighlightType = { ProblemHighlightType.INFORMATION },
    kClass = KtValueArgument::class.java,
    inspectionText = { "Not used at the moment because the highlight type used is ProblemHighlightType.INFORMATION" },
    isApplicable = { ktCall: KtValueArgument ->
      ktCall.ctx()?.let { compilerContext ->
        ktCall.participatingTypes()?.let { (subtype, supertype) ->
          compilerContext.areTypesCoerced(subtype, supertype)
        }
      } ?: false
    },
    applyTo = { ktCall: KtValueArgument, _, _ ->
      ktCall.ctx()?.let { compilerContext ->
        ktCall.makeExplicit(compilerContext)
      }
    },
    enabledByDefault = true
  )

const val COERCION_EXPLICIT_PROP = "Coercion_explicit_prop"

val IdeSyntax.explicitCoercionKtProperty: AbstractApplicabilityBasedInspection<KtProperty>
  get() = applicableInspection(
    defaultFixText = COERCION_EXPLICIT_PROP,
    inspectionHighlightType = { ProblemHighlightType.INFORMATION },
    kClass = KtProperty::class.java,
    inspectionText = { "Not used at the moment because the highlight type used is ProblemHighlightType.INFORMATION" },
    isApplicable = { ktCall: KtProperty ->
      ktCall.ctx()?.let { compilerContext ->
        ktCall.participatingTypes()?.let { (subtype, supertype) ->
          compilerContext.areTypesCoerced(subtype, supertype)
        }
      } ?: false
    },
    applyTo = { ktCall: KtProperty, _, _ ->
      ktCall.ctx()?.let { compilerContext ->
        ktCall.makeExplicit(compilerContext)
      }
    },
    enabledByDefault = true
  )

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
