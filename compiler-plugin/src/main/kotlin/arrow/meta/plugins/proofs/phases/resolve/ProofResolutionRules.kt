package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.AnalysisHandler
import arrow.meta.plugins.proofs.phases.isProof
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageUtil
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.descriptorUtil.isPublishedApi
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

internal fun Meta.proofResolutionRules(): AnalysisHandler =
  analysis(
    doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
      resolutionRules(bindingTrace)
      null
    },
    analysisCompleted = { project, module, bindingTrace, files ->
      //resolutionRules(bindingTrace)
      null
    }
  )

internal fun CompilerContext.resolutionRules(trace: BindingTrace) {
  val proofs =
    trace.bindingContext.getSliceContents(BindingContext.DECLARATION_TO_DESCRIPTOR)
  proofs.forEach { (element, descriptor) ->
    descriptor.takeIf {
      it.isProof() && it.isPublishedApi() &&
        element.safeAs<KtDeclaration>()?.hasModifier(KtTokens.INTERNAL_KEYWORD) ?: false
    }?.run {
      messageCollector?.report(
        CompilerMessageSeverity.ERROR,
        "Internal overrides of proofs are not permitted to be published, due to coherence reasons. Please remove the @PublishedApi annotation.",
        MessageUtil.psiElementToMessageLocation(element)
      )
    }
  }
}