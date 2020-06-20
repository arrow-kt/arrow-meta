package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.Meta
import arrow.meta.diagnostic.MetaErrors
import arrow.meta.internal.Noop
import arrow.meta.phases.analysis.AnalysisHandler
import arrow.meta.phases.analysis.traverseFilter
import arrow.meta.plugins.proofs.phases.hasAnnotation
import arrow.meta.plugins.proofs.phases.isProof
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace

internal fun Meta.proofResolutionRules(): AnalysisHandler =
  analysis(
    doAnalysis = Noop.nullable7(),
    analysisCompleted = { _, _, bindingTrace, files ->
      resolutionRules(bindingTrace, files)
      null
    }
  )

internal fun resolutionRules(trace: BindingTrace, files: Collection<KtFile>) {
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