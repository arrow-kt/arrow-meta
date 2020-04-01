package arrow.meta.ide.plugins.proofs.intentions

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.plugins.proofs.intentions.PairTypes.Companion.pairOrNull
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.ElementScope
import arrow.meta.plugins.proofs.phases.areTypesCoerced
import arrow.meta.plugins.proofs.phases.coerceProof
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.debugger.sequence.psi.resolveType
import org.jetbrains.kotlin.idea.imports.importableFqName
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.ImportPath
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.types.KotlinType

fun IdeMetaPlugin.makeExplicitCoercionIntention(compilerContext: CompilerContext): ExtensionPhase =
  compilerContext.run {
    addIntention(
      text = "Make coercion explicit",
      kClass = KtElement::class.java,
      isApplicableTo = { ktCall: KtElement, _ ->
        //TODO we should handle the caret here to not show the intention for all KtCallElement args,
        // but only the implicit ones
        ktCall.explicitParticipatingTypes().any { (subtype, supertype) ->
          compilerContext.areTypesCoerced(subtype, supertype)
        }
      },
      applyTo = { ktCall: KtElement, _ ->
        when (ktCall) {
          //TODO this should only modify one element at a time
          is KtCallElement -> {
            // Get the coerced types from all arguments
            val targetingTypes: List<PairTypes> = ktCall.explicitParticipatingTypes()
              .filter { (subtype, supertype) -> compilerContext.areTypesCoerced(subtype, supertype) }

            // get all coerced call element args
            ktCall.valueArgumentList?.arguments?.mapNotNull { it.getArgumentExpression() }
              ?.mapNotNull { arg ->
                val type = arg.resolveType()
                targetingTypes.firstOrNull { it.subType == type }?.let { targetType ->
                  targetType to arg
                }
              }
              // replace them with the explicit version with the proof
              ?.forEach { (targetType, arg) ->
                replaceWithProof(arg, compilerContext, targetType)
              }
          }

          is KtProperty -> {
            ktCall.explicitParticipatingTypes().first().let { pairType ->
              ktCall.initializer?.let { initializer ->
                replaceWithProof(initializer, compilerContext, pairType)
              }
            }
          }

        }
      }
    )
  }

private fun ElementScope.replaceWithProof(element: KtExpression, compilerContext: CompilerContext, pairType: PairTypes) {
  val through = compilerContext.coerceProof(pairType.subType, pairType.superType)!!.through
  val proofImport = through.importableFqName?.let {
    importDirective(ImportPath(it, false)).value
  }!!
  val importList = element.containingKtFile.importList!!
  //TODO deal with nullability && don't add import if it isn't necessary (same pkg or already imported)
  importList.add(proofImport as PsiElement)// TODO sort?
  val ktExpression: KtExpression? = "${element.text}.${through.name}()".expression.value
  ktExpression?.let(element::replace)
}

//TODO move elsewhere
data class PairTypes(val subType: KotlinType, val superType: KotlinType) {
  companion object {
    infix fun KotlinType?.pairOrNull(b: KotlinType?): PairTypes? =
      if (this != null && b != null) PairTypes(this, b)
      else null
  }
}

fun KtElement.explicitParticipatingTypes(): List<PairTypes> =
  when (this) {
    is KtCallElement -> {
      // Obtain the argument types from the current call
      val subTypes = valueArgumentList?.arguments.orEmpty().mapNotNull { it.getArgumentExpression()?.resolveType() }

      val superTypes = analyze(bodyResolveMode = BodyResolveMode.FULL)
        .getSliceContents(BindingContext.RESOLVED_CALL)
        // get the calls for the current element
        .filter { (call, _) -> call.callElement == this }
        // then the argument types
        .entries.first().value.valueArguments
        .map { it.key.type }

      //TODO check for named and switched arguments!

      subTypes.zip(superTypes, ::PairTypes)
    }

    is KtProperty -> {
      val superType = type()
      val subType = initializer?.resolveType()
      listOfNotNull((subType pairOrNull superType))
    }
    else -> emptyList()
  }
