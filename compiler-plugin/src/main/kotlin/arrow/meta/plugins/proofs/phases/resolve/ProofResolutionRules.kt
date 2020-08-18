package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.Meta
import arrow.meta.diagnostic.MetaErrors
import arrow.meta.diagnostic.MetaErrors.AmbiguousExtensionProof
import arrow.meta.diagnostic.MetaErrors.OwnershipViolatedProof
import arrow.meta.internal.Noop
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.AnalysisHandler
import arrow.meta.phases.analysis.exists
import arrow.meta.phases.analysis.traverseFilter
import arrow.meta.plugins.proofs.phases.ExtensionProof
import arrow.meta.plugins.proofs.phases.GivenProof
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.RefinementProof
import arrow.meta.plugins.proofs.phases.extensionProofs
import arrow.meta.plugins.proofs.phases.hasAnnotation
import arrow.meta.plugins.proofs.phases.isProof
import arrow.meta.plugins.proofs.phases.proof
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.isPublic
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

internal fun Meta.proofResolutionRules(): AnalysisHandler =
  analysis(
    doAnalysis = Noop.nullable7(),
    analysisCompleted = { _, _, bindingTrace, files ->
      resolutionRules(bindingTrace, files)
      null
    }
  )

internal fun CompilerContext.resolutionRules(trace: BindingTrace, files: Collection<KtFile>): Unit {
  // General rules
  files.forEach { file: KtFile ->
    reportProhibitedPublishedInternalOrphans(trace, file)
    reportOwnershipViolations(trace, file)
  }
  // Rule-set for ExtensionProofs
  extensionProofs().run {
    reportDisallowedUserDefinedAmbiguities(trace)
    reportSkippedProofsDueToAmbiguities { proof, ambeguities ->
      messageCollector?.report(CompilerMessageSeverity.ERROR, "Please Provide an internal Proof")
        ?: println("TODO for skipped Proofs:$proof with ambeguities:$ambeguities")
    }
  }
  // Rule-set for GivenProofs
  // TODO: Semi-inductive resolution rules e.g.: missing given()
  // TODO: Ambiguities
  // Rule-set for RefinementProofs
  // TODO: Ambiguities
}

fun prohibitedPublishedInternalOrphans(trace: BindingTrace, file: KtFile): List<KtDeclaration> =
  file.traverseFilter(KtDeclaration::class.java) { declaration ->
    declaration.isPublishedInternalOrphan(trace)
  }

fun KtDeclaration.isPublishedInternalOrphan(trace: BindingTrace): KtDeclaration? =
  takeIf {
    it.isProof(trace) &&
      it.hasAnnotation(trace, KotlinBuiltIns.FQ_NAMES.publishedApi) &&
      it.hasModifier(KtTokens.INTERNAL_KEYWORD)
  }

fun CompilerContext.ownershipViolations(trace: BindingTrace, file: KtFile): List<Pair<KtDeclaration, Proof>> =
  file.traverseFilter(KtDeclaration::class.java) { declaration ->
    declaration.isViolatingOwnershipRule(trace, this)
  }

fun KtDeclaration.isViolatingOwnershipRule(trace: BindingTrace, ctx: CompilerContext): Pair<KtDeclaration, Proof>? =
  takeIf { it.isProof(trace) }?.let {
    ctx.proof<Proof>().firstOrNull {
      it.through == trace.bindingContext.get(BindingContext.DECLARATION_TO_DESCRIPTOR, this)
    }?.takeIf {
      !hasModifier(KtTokens.INTERNAL_KEYWORD) &&
        (when (it) {
          is ExtensionProof -> !it.from.isUserOwned() || !it.to.isUserOwned()
          is GivenProof -> !it.to.isUserOwned()
          is RefinementProof -> !it.from.isUserOwned() || !it.to.isUserOwned()
        })
    }?.let {
      this to it
    }
  }


/**
 * A type is user-owned, when at least one position of the type signature is a user type in the sources.
 * e.g.: `org.core.Semigroup<A, F>` materialises into `A -> F -> org.core.Semigroup<A, F>`
 * Thereby the user needs to own either `F`, `A` or `org.core.Semigroup` to publish a proof.
 */
fun KotlinType.isUserOwned(): Boolean =
  hasSource() || arguments.any { it.type.hasSource() }

fun KotlinType.hasSource(): Boolean =
  constructor.declarationDescriptor?.source != SourceElement.NO_SOURCE

fun Map<Pair<KotlinType, KotlinType>, List<ExtensionProof>>.disallowedAmbiguities(): List<Pair<ExtensionProof, List<ExtensionProof>>> =
  mapNotNull { (_, proofs) ->
    proofs.exists { p1, p2 ->
      (p1.through.visibility == Visibilities.PUBLIC && p2.through.visibility == Visibilities.PUBLIC
        )
        || (p1.through.visibility == Visibilities.INTERNAL && p2.through.visibility == Visibilities.INTERNAL)
      // TODO: Loosen the rule to allow package scoped proofs when they have the same package-info
    }.filter { (_, v) -> v.isNotEmpty() } // filter out proofs with conflicts
  }.flatten()

fun Map<Pair<KotlinType, KotlinType>, List<ExtensionProof>>.disallowedUserDefinedAmbiguities(): List<Pair<Pair<ExtensionProof, KtNamedFunction>, List<ExtensionProof>>> =
  disallowedAmbiguities().mapNotNull { (proof, others) -> // collect those with a SourceElement, which the User is responsible of
    proof.through.findPsi().safeAs<KtNamedFunction>()?.let {
      (proof to it) to others
    }
  }

/**
 * additionally to the Docs in [reportDisallowedUserDefinedAmbiguities].
 * The following extensions sends warnings, which proofs are being skipped and a prompt to the user to define an internal orphan to resolve coherence.
 */
fun Map<Pair<KotlinType, KotlinType>, List<ExtensionProof>>.skippedProofsDueToAmbiguities() =
  disallowedAmbiguities().filter { (proof, others) -> // collect those without a SourceElement, which the user is needs to override
    with(proof.through) {
      findPsi().safeAs<KtNamedFunction>() == null &&
        (visibility == Visibilities.INTERNAL // case: instrumented dependencies that publish internal proofs
          || visibility == Visibilities.PUBLIC) && others.any {// case: local project publishes public proof over non-owned types
        with(it.through) {
          visibility == Visibilities.PUBLIC && it.through.findPsi().safeAs<KtNamedFunction>() == null
        }
      }
    }
  }

/**
 * the strategy that follows is that authors are responsible for a coherent resolution of the project.
 * Either by disambiguating proofs through a proper scope or disambiguating dependency proofs that lead to undefined behavior of proof resolution for the project or 3rd parties depending on coherence.
 */
private fun Map<Pair<KotlinType, KotlinType>, List<ExtensionProof>>.reportDisallowedUserDefinedAmbiguities(trace: BindingTrace): Unit =
  disallowedUserDefinedAmbiguities().toMap()
    .forEach { (proof, f), conflicts ->
      trace.report(AmbiguousExtensionProof.on(f, proof, conflicts))
      //println("Proof:$proof in ${f.name} is ambiguous in respect to ${conflicts.joinToString(separator = "\n") { "Proof: ${it.through.name.asString()}" }}")
    }

/**
 * Internal proofs are not permitted to be published, due to coherence reasons leading to ambiguities.
 */
private fun reportProhibitedPublishedInternalOrphans(trace: BindingTrace, file: KtFile): Unit =
  prohibitedPublishedInternalOrphans(trace, file).forEach {
    trace.report(MetaErrors.PublishedInternalOrphan.on(it))
  }

/**
 * Public Proofs are only valid, if they don't impose inconsistencies in the resolution process.
 * That is the associated type or types in the proof have to be user owned.
 * @see isUserOwned
 */
private fun CompilerContext.reportOwnershipViolations(trace: BindingTrace, file: KtFile): Unit =
  ownershipViolations(trace, file).forEach { (declaration, proof) ->
    trace.report(OwnershipViolatedProof.on(declaration, proof))
  }

private fun Map<Pair<KotlinType, KotlinType>, List<ExtensionProof>>.reportSkippedProofsDueToAmbiguities(
  f: (proof: ExtensionProof, ambiguities: List<ExtensionProof>) -> Unit
): Unit =
  skippedProofsDueToAmbiguities().toMap().forEach(f)