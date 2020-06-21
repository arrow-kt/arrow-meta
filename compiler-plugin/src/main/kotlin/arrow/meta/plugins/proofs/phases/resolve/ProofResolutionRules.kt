package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.Meta
import arrow.meta.diagnostic.MetaErrors
import arrow.meta.diagnostic.MetaErrors.AmbiguousExtensionProof
import arrow.meta.internal.Noop
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.AnalysisHandler
import arrow.meta.phases.analysis.exists
import arrow.meta.phases.analysis.traverseFilter
import arrow.meta.plugins.proofs.phases.ExtensionProof
import arrow.meta.plugins.proofs.phases.extensionProofs
import arrow.meta.plugins.proofs.phases.hasAnnotation
import arrow.meta.plugins.proofs.phases.isProof
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
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
  extensionProofs()
    .disallowAmbiguity(trace)
  files.forEach { file: KtFile ->
    prohibitPublishedInternalOrphans(trace, file)
  }
}

private fun prohibitPublishedInternalOrphans(trace: BindingTrace, file: KtFile): Unit =
  file.traverseFilter(KtDeclaration::class.java) { declaration ->
    declaration.takeIf {
      it.isProof(trace) &&
        it.hasAnnotation(trace, KotlinBuiltIns.FQ_NAMES.publishedApi) &&
        it.hasModifier(KtTokens.INTERNAL_KEYWORD)
    }
  }.forEach {
    trace.report(MetaErrors.PublishedInternalOrphan.on(it))
  }

/**
 * the strategy that follows is that authors are responsible for a coherent resolution of the project.
 * Either by disambiguating proofs through a proper scope or removing dependencies that lead to undefined behavior of proof resolution for the project or 3rd parties depending on coherence.
 */
private fun Map<Pair<KotlinType, KotlinType>, List<ExtensionProof>>.disallowAmbiguity(trace: BindingTrace): Unit =
  forEach { (_, _), proofs ->
    val ambiguousProofs = proofs.exists { p1, p2 ->
      (p1.through.visibility == Visibilities.PUBLIC && p2.through.visibility == Visibilities.PUBLIC
        )
        || (p1.through.visibility == Visibilities.INTERNAL && p2.through.visibility == Visibilities.INTERNAL)
      // TODO: Loosen the rule to allow package scoped proofs when they have the same package-info
    }.filter { (_, v) -> v.isNotEmpty() } // filter out proofs with conflicts

    // user-defined ambiguousProofs
    ambiguousProofs
      .mapNotNull { (proof, others) -> // collect those with a SourceElement, which the User is responsible of
        proof.through.findPsi().safeAs<KtNamedFunction>()?.let {
          (proof to it) to others
        }
      }.toMap().forEach { (proof, f), conflicts ->
        trace.report(AmbiguousExtensionProof.on(f, proof, conflicts))
        //println("Proof:$proof in ${f.name} is ambiguous in respect to ${conflicts.joinToString(separator = "\n") { "Proof: ${it.through.name.asString()}" }}")
      }
  }