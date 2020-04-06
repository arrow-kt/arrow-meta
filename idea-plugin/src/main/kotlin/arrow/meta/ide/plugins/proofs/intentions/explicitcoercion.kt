package arrow.meta.ide.plugins.proofs.intentions

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import arrow.meta.plugins.proofs.phases.coerceProof
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.debugger.sequence.psi.resolveType
import org.jetbrains.kotlin.idea.imports.importableFqName
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.ImportPath

fun IdeMetaPlugin.makeExplicitCoercionIntention(compilerContext: CompilerContext): ExtensionPhase =
  compilerContext.run {
    addApplicableInspection(
      defaultFixText = "Make_coercion_explicit",
      enabledByDefault = false,
      kClass = KtElement::class.java,
      isApplicable = { ktCall: KtElement ->
        ktCall.explicitParticipatingTypes().any { (subtype, supertype) ->
          compilerContext.areTypesCoerced(subtype, supertype)
        }
      },
      applyTo = { ktCall: KtElement, _, _ ->
        ktCall.makeExplicit(compilerContext)
      },
      inspectionText = { "TODO explicit " },
      inspectionHighlightType = { ProblemHighlightType.INFORMATION },
      groupPath = ArrowPath + arrayOf("Coercion")
    )
  }

private fun KtElement.makeExplicit(compilerContext: CompilerContext) {
  when (this) {
    is KtCallElement -> {
      // Get the coerced types from all arguments
      val targetingTypes: List<PairTypes> = explicitParticipatingTypes()
        .filter { (subtype, supertype) -> compilerContext.areTypesCoerced(subtype, supertype) }

      // get all coerced call element args
      valueArgumentList?.arguments?.mapNotNull { it.getArgumentExpression() }
        ?.mapNotNull { arg ->
          val type = arg.resolveType()
          targetingTypes.firstOrNull { it.subType == type }?.let { targetType ->
            targetType to arg
          }
        }
        // replace them with the explicit version with the proof
        ?.forEach { (targetType, arg) ->
          arg.replaceWithProof(compilerContext, targetType)
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

  val notImported = !importList.imports.any { it.importedFqName == importableFqName }
  val differentPackage = containingKtFile.packageFqName != importableFqName

  if (notImported && differentPackage) {
    val proofImport = importableFqName?.let {
      importDirective(ImportPath(it, false)).value
    }!!
    //TODO deal with nullability
    importList.add(proofImport as PsiElement)// TODO sort?
  }
  val ktExpression: KtExpression? = "$text.${through.name}()".expression.value
  ktExpression?.let(::replace)
}

