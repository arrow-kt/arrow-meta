package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.Meta
import arrow.meta.diagnostic.MetaErrors
import arrow.meta.diagnostic.MetaErrors.AmbiguousProof
import arrow.meta.diagnostic.MetaErrors.OwnershipViolatedProof
import arrow.meta.internal.Noop
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.AnalysisHandler
import arrow.meta.phases.analysis.exists
import arrow.meta.phases.analysis.traverseFilter
import arrow.meta.plugins.proofs.phases.ArrowGivenProof
import arrow.meta.plugins.proofs.phases.CallableMemberProof
import arrow.meta.plugins.proofs.phases.ClassProof
import arrow.meta.plugins.proofs.phases.ExtensionProof
import arrow.meta.plugins.proofs.phases.GivenProof
import arrow.meta.plugins.proofs.phases.ObjectProof
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.RefinementProof
import arrow.meta.plugins.proofs.phases.allGivenProofs
import arrow.meta.plugins.proofs.phases.extensionProofs
import arrow.meta.plugins.proofs.phases.hasAnnotation
import arrow.meta.plugins.proofs.phases.isProof
import arrow.meta.plugins.proofs.phases.proof
import arrow.meta.plugins.proofs.phases.refinementProofs
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithVisibility
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
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
    reportSkippedProofsDueToAmbiguities { proof, ambiguities ->
      messageCollector?.report(CompilerMessageSeverity.ERROR, "Please Provide an internal Proof")
        ?: println("TODO for skipped Proof:$proof with ambiguities:$ambiguities")
    }
  }
  // Rule-set for GivenProofs
  allGivenProofs().run {
    reportUnresolvedGivenProofs(trace, messageCollector)
    reportDisallowedUserDefinedAmbiguities(trace)
    reportSkippedProofsDueToAmbiguities { proof, ambiguities ->
      messageCollector?.report(CompilerMessageSeverity.ERROR, "Please Provide an internal Proof")
        ?: println("TODO for skipped Proofs:$proof with ambeguities:$ambiguities")
    }
  }
  // Rule-set for RefinementProofs
  refinementProofs().run {
    // TODO: correct refinement proofs

    reportDisallowedUserDefinedAmbiguities(trace)
    reportSkippedProofsDueToAmbiguities { proof, ambiguities ->
      messageCollector?.report(CompilerMessageSeverity.ERROR, "Please Provide an internal Proof")
        ?: println("TODO for skipped Proofs:$proof with ambeguities:$ambiguities")
    }
  }
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
          is ExtensionProof ->
            when (it.from.isUserOwned() xor it.to.isUserOwned()) {
              true -> false // Proof is not violating ownership
              false -> !it.from.isUserOwned() && !it.to.isUserOwned() // Proofs over user-owned Types don't break ownership
            }
          is GivenProof -> !it.to.isUserOwned()
          is RefinementProof ->
            when (it.from.isUserOwned() xor it.to.isUserOwned()) {
              true -> false // Proof is not violating ownership
              false -> !it.from.isUserOwned() && !it.to.isUserOwned() // Proofs over user-owned Types don't break ownership
            }
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

fun <K, A : Proof> Map<K, List<A>>.disallowedAmbiguities(): List<Pair<A, List<A>>> =
  mapNotNull { (_, proofs) ->
    proofs.exists { p1, p2 ->
      val a = p1.through.safeAs<DeclarationDescriptorWithVisibility>()?.visibility
      val b = p2.through.safeAs<DeclarationDescriptorWithVisibility>()?.visibility
      a == Visibilities.PUBLIC && b == Visibilities.PUBLIC
        || (a == Visibilities.INTERNAL && b == Visibilities.INTERNAL)
      // TODO: Loosen the rule to allow package scoped proofs when they have the same package-info
    }.filter { (_, v) -> v.isNotEmpty() } // filter out proofs with conflicts
  }.flatten()

fun <K, A : Proof> Map<K, List<A>>.disallowedUserDefinedAmbiguities(): List<Pair<Pair<A, KtDeclaration>, List<A>>> =
  disallowedAmbiguities().mapNotNull { (proof, others) -> // collect those with a SourceElement, which the User is responsible of
    proof.through.findPsi().safeAs<KtDeclaration>()?.let {
      (proof to it) to others
    }
  }

/**
 * additionally to the Docs in [reportDisallowedUserDefinedAmbiguities].
 * The following extensions sends warnings, which proofs are being skipped and a prompt to the user to define an internal orphan to resolve coherence.
 */
fun <K, A : Proof> Map<K, List<A>>.skippedProofsDueToAmbiguities() =
  disallowedAmbiguities().filter { (proof, others) -> // collect those without a SourceElement, which the user is needs to override
    with(proof.through as DeclarationDescriptorWithVisibility) {
      findPsi() == null &&
        (visibility == Visibilities.INTERNAL // case: instrumented dependencies that publish internal proofs
          || visibility == Visibilities.PUBLIC) && others.any {// case: local project publishes public proof over non-owned types
        with(it.through) {
          visibility == Visibilities.PUBLIC && findPsi() == null
        }
      }
    }
  }

/**
 * `unresolved` that is a GivenProof that has Parameters e.g.: [ClassProof], [CallableMemberProof] and
 * those don't have default values or are not being semi-inductively resolved by other GivenProof's.
 */
fun Map<KotlinType, List<GivenProof>>.unresolvedGivenProofs(): Map<KotlinType, List<GivenProof>> =
  mapValues { (type, proofs) ->
    proofs.filter { !it.isResolved(this) }
  }.filter { (_, unresolved) ->
    unresolved.isNotEmpty()
  }

fun GivenProof.isResolved(others: Map<KotlinType, List<GivenProof>>): Boolean =
  when (this) {
    is ObjectProof -> true // object proofs are resolved automatically, as they do not have constructors
    is ClassProof -> isResolved(others)
    is CallableMemberProof -> isResolved(others)
  }

/**
 * which constructor is chosen in IR?
 * TODO: Check if the defaultValue is resolved
 */
fun ClassProof.isResolved(others: Map<KotlinType, List<GivenProof>>): Boolean =
  through.constructors.all { f ->
    f.valueParameters.all { param ->
      if (param.type.annotations.hasAnnotation(ArrowGivenProof))
        others.getOrDefault(param.type, emptyList()).any { it.isResolved(others) }
      else param.declaresDefaultValue()
    }
  }

/**
 * TODO: Check if the defaultValue is resolved
 */
fun CallableMemberProof.isResolved(others: Map<KotlinType, List<GivenProof>>): Boolean =
  through.valueParameters.all { param ->
    if (param.type.annotations.hasAnnotation(ArrowGivenProof))
      others.getOrDefault(param.type, emptyList()).any { it.isResolved(others) }
    else
      true
  }

fun Map<KotlinType, List<GivenProof>>.reportUnresolvedGivenProofs(trace: BindingTrace, msg: MessageCollector?): Unit =
  unresolvedGivenProofs().forEach { (type, proofs) ->
    proofs.forEach { proof ->
      proof.through.findPsi()?.safeAs<KtDeclaration>()?.let { element ->
        trace.report(MetaErrors.UnresolvedGivenProof.on(element, type))
      } ?: msg?.report(CompilerMessageSeverity.WARNING,
        "The GivenProof ${proof.through.fqNameSafe.asString()} from the project dependencies on the type ${DescriptorRenderer.FQ_NAMES_IN_TYPES.renderType(type)} can't be semi-inductively resolved and won't be considered in resolution.")
    }
  }


/**
 * the strategy that follows is that authors are responsible for a coherent resolution of the project.
 * Either by disambiguating proofs through a proper scope or disambiguating dependency proofs that lead to undefined behavior of proof resolution for the project or 3rd parties depending on coherence.
 */
private fun <K, A : Proof> Map<K, List<A>>.reportDisallowedUserDefinedAmbiguities(trace: BindingTrace): Unit =
  disallowedUserDefinedAmbiguities().toMap()
    .forEach { (proof, f), conflicts ->
      trace.report(AmbiguousProof.on(f, proof, conflicts))
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

private fun <K, A : Proof> Map<K, List<A>>.reportSkippedProofsDueToAmbiguities(
  f: (proof: A, ambiguities: List<A>) -> Unit
): Unit =
  skippedProofsDueToAmbiguities().toMap().forEach(f)