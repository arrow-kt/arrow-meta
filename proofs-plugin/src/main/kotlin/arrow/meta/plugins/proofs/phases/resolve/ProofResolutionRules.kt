package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.Meta
import arrow.meta.diagnostic.MetaErrors
import arrow.meta.diagnostic.MetaErrors.AmbiguousProof
import arrow.meta.diagnostic.MetaErrors.OwnershipViolatedProof
import arrow.meta.diagnostic.MetaErrors.UnresolvedGivenCallSite
import arrow.meta.diagnostic.MetaErrors.UnresolvedGivenProof
import arrow.meta.internal.Noop
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.exists
import arrow.meta.phases.analysis.traverseFilter
import arrow.meta.plugins.proofs.phases.ArrowCompileTime
import arrow.meta.plugins.proofs.phases.CallableMemberProof
import arrow.meta.plugins.proofs.phases.ClassProof
import arrow.meta.plugins.proofs.phases.GivenProof
import arrow.meta.plugins.proofs.phases.ObjectProof
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.allGivenProofs
import arrow.meta.plugins.proofs.phases.contextualAnnotations
import arrow.meta.plugins.proofs.phases.givenProof
import arrow.meta.plugins.proofs.phases.hasAnnotation
import arrow.meta.plugins.proofs.phases.isGivenContextProof
import arrow.meta.plugins.proofs.phases.isProof
import arrow.meta.plugins.proofs.phases.proof
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithVisibility
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.checkers.CallCheckerContext
import org.jetbrains.kotlin.resolve.calls.model.DefaultValueArgument
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedContainerSource
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter
import org.jetbrains.kotlin.types.typeUtil.isUnit
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

internal fun Meta.proofResolutionRules(): ExtensionPhase =
  Composite(
    analysis(
      doAnalysis = Noop.nullable7(),
      analysisCompleted = { _, _, bindingTrace, files ->
        resolutionRules(bindingTrace, files)
        null
      }
    ),
    callChecker { resolvedCall, reportOn, context ->
      callSiteResolution(resolvedCall, reportOn, context)
    }
  )

internal fun CompilerContext.resolutionRules(trace: BindingTrace, files: Collection<KtFile>): Unit {
  // General rules
  files.forEach { file: KtFile ->
    reportProhibitedPublishedInternalOrphans(trace, file)
    reportOwnershipViolations(trace, file)
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
}

internal fun CompilerContext.callSiteResolution(
  resolvedCall: ResolvedCall<*>,
  reportOn: PsiElement,
  context: CallCheckerContext
): Unit =
  reportOn.parent.safeAs<KtExpression>()?.let {
    reportUnresolvedGivenCallSite(resolvedCall, it, context.trace)
  } ?: Unit

val ClassDescriptor.inlinedType: KotlinType?
  get() = takeIf { it.isInline }?.run {
    unsubstitutedPrimaryConstructor?.valueParameters?.first()?.type
  }

fun CompilerContext.unresolvedGivenCallSite(call: ResolvedCall<*>): List<ValueParameterDescriptor> =
  call.resultingDescriptor
    .valueParameters.filter { v ->
      val contextFqName = v.contextualAnnotations().firstOrNull()
      v.containingDeclaration.annotations.hasAnnotation(ArrowCompileTime) &&
        contextFqName != null &&
        givenProof(contextFqName, v.type) == null &&
        call.valueArguments[v] == DefaultValueArgument.DEFAULT &&
        !v.type.isUnit()
    }

fun prohibitedPublishedInternalOrphans(bindingContext: BindingContext, file: KtFile): List<KtDeclaration> =
  file.traverseFilter(KtDeclaration::class.java) { declaration ->
    declaration.isPublishedInternalOrphan(bindingContext)
  }

fun KtDeclaration.isPublishedInternalOrphan(bindingContext: BindingContext): KtDeclaration? =
  takeIf {
    it.isProof(bindingContext) &&
      it.hasAnnotation(bindingContext, StandardNames.FqNames.publishedApi) &&
      it.hasModifier(KtTokens.INTERNAL_KEYWORD)
  }

fun CompilerContext.ownershipViolations(trace: BindingContext, file: KtFile): List<Pair<KtDeclaration, Proof>> =
  file.traverseFilter(KtDeclaration::class.java) { declaration ->
    declaration.isViolatingOwnershipRule(trace, this)
  }

fun KtDeclaration.isViolatingOwnershipRule(
  bindingContext: BindingContext,
  ctx: CompilerContext
): Pair<KtDeclaration, Proof>? =
  takeIf { it.isProof(bindingContext) }?.let {
    ctx.proof<Proof>().firstOrNull {
      it.through == bindingContext.get(BindingContext.DECLARATION_TO_DESCRIPTOR, this)
    }?.takeIf {
      !hasModifier(KtTokens.INTERNAL_KEYWORD) &&
        (when (it) {
          is GivenProof -> !it.to.isUserOwned()
        })
    }?.let {
      this to it
    }
  }


/**
 * A type is user-owned, when at least one position of the type signature is a user type in the sources.
 * e.g.: `org.core.Semigroup<A, F>` materialises into `A -> F -> org.core.Semigroup<A, F>`
 * Thereby the user needs to own either `F`, `A` or `org.core.Semigroup` to publish a proof.
 * `F` or `A` can't be type parameters to be user-owned.
 */
fun KotlinType.isUserOwned(): Boolean =
  (hasUserSource() && !isTypeParameter()) || arguments.any { it.type.isUserOwned() }

fun KotlinType.hasUserSource(): Boolean =
  constructor.declarationDescriptor?.run { source !is DeserializedContainerSource && source != SourceElement.NO_SOURCE }
    ?: false

fun <K, A : Proof> Map<K, List<A>>.disallowedAmbiguities(): List<Pair<A, List<A>>> =
  mapNotNull { (_, proofs) ->
    proofs.exists { p1, p2 ->
      val a = p1.through.safeAs<DeclarationDescriptorWithVisibility>()?.visibility
      val b = p2.through.safeAs<DeclarationDescriptorWithVisibility>()?.visibility
      (a == DescriptorVisibilities.PUBLIC && b == DescriptorVisibilities.PUBLIC
        || (a == DescriptorVisibilities.INTERNAL && b == DescriptorVisibilities.INTERNAL))
        && p1.isContextAmbiguous(p2)
      // TODO: Loosen the rule to allow package scoped proofs when they have the same package-info
    }.filter { (_, v) -> v.isNotEmpty() } // filter out proofs with conflicts
  }.flatten()

fun <K, A : Proof> Map<K, List<A>>.disallowedUserDefinedAmbiguities(): List<Pair<Pair<A, KtDeclaration>, List<A>>> =
  disallowedAmbiguities().mapNotNull { (proof, others) ->
    // collect those with a SourceElement, which the User is responsible of
    proof.through.findPsi().safeAs<KtDeclaration>()?.let {
      (proof to it) to others
    }
  }

/**
 * additionally to the Docs in [reportDisallowedUserDefinedAmbiguities].
 * The following extensions sends warnings, which proofs are being skipped and a prompt to the user to define an internal orphan to resolve coherence.
 */
fun <K, A : Proof> Map<K, List<A>>.skippedProofsDueToAmbiguities(): List<Pair<A, List<A>>> =
  disallowedAmbiguities().filter { (proof, others) ->
    // collect those without a SourceElement, which the user is needs to override
    with(proof.through as DeclarationDescriptorWithVisibility) {
      findPsi() == null &&
        (visibility == DescriptorVisibilities.INTERNAL // case: instrumented dependencies that publish internal proofs
          || visibility == DescriptorVisibilities.PUBLIC) && others.any {
        // case: local project publishes public proof over non-owned types
        with(it.through) {
          visibility == DescriptorVisibilities.PUBLIC && findPsi() == null
        }
      }
    }
  }

/**
 * `unresolved` that is a GivenProof that has Parameters e.g.: [ClassProof], [CallableMemberProof] and
 * those don't have default values or are not being semi-inductively resolved by other GivenProof's.
 */
fun Map<KotlinType, List<GivenProof>>.unresolvedGivenProofs(): Map<KotlinType, List<GivenProof>> =
  mapValues { (_, proofs) ->
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
 * in IR the primaryConstructor is chosen see [arrow.meta.plugins.proofs.phases.resolve.asGivenProof]
 * TODO: Check if the defaultValue is resolved
 */
fun ClassProof.isResolved(others: Map<KotlinType, List<GivenProof>>): Boolean =
  through.unsubstitutedPrimaryConstructor?.valueParameters?.all { param ->
    if (param.annotations.any { it.isGivenContextProof() })
      others.getOrDefault(param.type, emptyList()).any { it.isResolved(others) }
    else param.declaresDefaultValue()
  } ?: false

/**
 * TODO: Check if the defaultValue is resolved
 */
fun CallableMemberProof.isResolved(others: Map<KotlinType, List<GivenProof>>): Boolean =
  through.valueParameters.all { param ->
    if (param.annotations.any { it.isGivenContextProof() })
      others.getOrDefault(param.type, emptyList()).any { it.isResolved(others) }
    else
      true
  }

fun Map<KotlinType, List<GivenProof>>.reportUnresolvedGivenProofs(trace: BindingTrace, msg: MessageCollector?): Unit =
  unresolvedGivenProofs().forEach { (type, proofs) ->
    proofs.forEach { proof ->
      proof.through.findPsi()?.safeAs<KtDeclaration>()?.let { element ->
        trace.report(UnresolvedGivenProof.on(element, type))
      } ?: msg?.report(
        CompilerMessageSeverity.WARNING,
        "The GivenProof ${proof.through.fqNameSafe.asString()} from the project dependencies on the type ${
          DescriptorRenderer.FQ_NAMES_IN_TYPES.renderType(
            type
          )
        } can't be semi-inductively resolved and won't be considered in resolution."
      )
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
  prohibitedPublishedInternalOrphans(trace.bindingContext, file).forEach {
    trace.report(MetaErrors.PublishedInternalOrphan.on(it))
  }

/**
 * Public Proofs are only valid, if they don't impose inconsistencies in the resolution process.
 * That is the associated type or types in the proof have to be user owned.
 * @see isUserOwned
 */
private fun CompilerContext.reportOwnershipViolations(trace: BindingTrace, file: KtFile): Unit =
  ownershipViolations(trace.bindingContext, file).forEach { (declaration, proof) ->
    trace.report(OwnershipViolatedProof.on(declaration, proof))
  }

private fun <K, A : Proof> Map<K, List<A>>.reportSkippedProofsDueToAmbiguities(
  f: (proof: A, ambiguities: List<A>) -> Unit
): Unit =
  skippedProofsDueToAmbiguities().toMap().forEach(f)

private fun CompilerContext.reportUnresolvedGivenCallSite(
  call: ResolvedCall<*>,
  element: KtExpression,
  trace: BindingTrace
): Unit =
  unresolvedGivenCallSite(call).let { values ->
    values.forEach {
      reportMissingInductiveDependencies(it, trace, element, call)
      trace.report(UnresolvedGivenCallSite.on(element, call, it.type))
    }
  }

private fun CompilerContext.reportMissingInductiveDependencies(
  it: ValueParameterDescriptor,
  trace: BindingTrace,
  element: KtExpression,
  call: ResolvedCall<*>
) {
  if (it.type.constructor.declarationDescriptor?.annotations?.any { it.isGivenContextProof() } == true) {
    val dcl = it.type.constructor.declarationDescriptor
    if (dcl is ClassDescriptor) {
      dcl.constructors.firstOrNull { it.isPrimary }?.valueParameters?.forEach { valueParam ->
        val contextFqName = valueParam.contextualAnnotations().firstOrNull()
        val parameterProof = contextFqName?.let { givenProof(it, valueParam.type) }
        if (parameterProof == null)
          trace.report(UnresolvedGivenCallSite.on(element, call, valueParam.type))
      }
    }
  }
}